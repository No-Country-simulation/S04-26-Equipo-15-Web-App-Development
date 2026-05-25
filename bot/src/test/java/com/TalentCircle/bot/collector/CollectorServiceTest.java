// =========================================================================
// SCRUM-15: Tests unitarios para CollectorService
// Verifica: extracción de num_comments, manejo de rate limits (429), errores genéricos
// =========================================================================

package com.TalentCircle.bot.collector;

import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.collector.dto.TopAnsweredQuestionDTO;
import com.TalentCircle.bot.collector.service.CollectorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectorServiceTest {

    private CollectorService collectorService;

    @Mock
    private RestTemplate mockRestTemplate;

    @BeforeEach
    void setUp() {
        collectorService = new CollectorService();
        // Inyectamos el RestTemplate mockeado y desactivamos el delay para que los tests sean rápidos
        ReflectionTestUtils.setField(collectorService, "restTemplate", mockRestTemplate);
        ReflectionTestUtils.setField(collectorService, "RetryDelayMs", 0);
    }

    @Test
    void getTopWeeklyResources_debeRetornarPostsConTodosLosCamposIncluidoNumComments() throws Exception {
        String json = "{\"data\":{\"children\":[{\"data\":{" +
                "\"title\":\"Java Tips 2025\"," +
                "\"url\":\"https://example.com/java\"," +
                "\"score\":1500," +
                "\"author\":\"dev1\"," +
                "\"subreddit\":\"java\"," +
                "\"selftext\":\"Contenido del post\"," +
                "\"num_comments\":42" +
                "}}]}}";
        JsonNode mockBody = new ObjectMapper().readTree(json);

        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(mockBody));

        List<WeeklyActivityDTO> result = collectorService.getTopWeeklyResourcesWithRateLimitHandling("java");

        assertThat(result).hasSize(1);
        WeeklyActivityDTO post = result.get(0);
        assertThat(post.getTitle()).isEqualTo("Java Tips 2025");
        assertThat(post.getUrl()).isEqualTo("https://example.com/java");
        assertThat(post.getScore()).isEqualTo(1500);
        assertThat(post.getAuthor()).isEqualTo("dev1");
        assertThat(post.getNumComments()).isEqualTo(42);
    }

    @Test
    void getTopWeeklyResources_conRateLimit429_debeReintentarExactamenteTresVeces() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        List<WeeklyActivityDTO> result = collectorService.getTopWeeklyResourcesWithRateLimitHandling("java");

        assertThat(result).isEmpty();
        // maxRetries = 3, por eso se llama exactamente 3 veces
        verify(mockRestTemplate, times(3)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class));
    }

    @Test
    void getTopWeeklyResources_conErrorGenerico_debeRetornarListaVaciaSinReintentar() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        List<WeeklyActivityDTO> result = collectorService.getTopWeeklyResourcesWithRateLimitHandling("java");

        assertThat(result).isEmpty();
        // Error genérico no reintenta, solo 1 intento
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class));
    }

    // =========================================================================
    // SCRUM-16: Tests para getMostAnsweredQuestionsOfWeek
    // =========================================================================

    @Test
    void getMostAnsweredQuestions_debeRetornarSoloPreguntasOrdenadasPorRespuestas() throws Exception {
        // Hay 3 posts: 2 preguntas y 1 que no es pregunta. Las preguntas deben salir ordenadas desc.
        String json = "{\"data\":{\"children\":[" +
                "{\"data\":{\"title\":\"Pregunta A?\",\"url\":\"https://reddit.com/a\",\"author\":\"user1\"," +
                "\"selftext\":\"\",\"num_comments\":10}}," +
                "{\"data\":{\"title\":\"Post sin pregunta\",\"url\":\"https://reddit.com/b\",\"author\":\"user2\"," +
                "\"selftext\":\"\",\"num_comments\":99}}," +
                "{\"data\":{\"title\":\"Pregunta B?\",\"url\":\"https://reddit.com/c\",\"author\":\"user3\"," +
                "\"selftext\":\"\",\"num_comments\":50}}" +
                "]}}";
        JsonNode mockBody = new ObjectMapper().readTree(json);

        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(mockBody));

        List<TopAnsweredQuestionDTO> result = collectorService.getMostAnsweredQuestionsOfWeek("learnprogramming");

        // Solo las 2 preguntas, la de mayor num_comments primero
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNumAnswers()).isEqualTo(50);
        assertThat(result.get(0).getTitle()).isEqualTo("Pregunta B?");
        assertThat(result.get(1).getNumAnswers()).isEqualTo(10);
    }

    @Test
    void getMostAnsweredQuestions_debeExcluirPostsEliminadosYModerados() throws Exception {
        String json = "{\"data\":{\"children\":[" +
                "{\"data\":{\"title\":\"Pregunta válida?\",\"url\":\"https://reddit.com/valid\"," +
                "\"author\":\"realUser\",\"selftext\":\"\",\"num_comments\":30}}," +
                "{\"data\":{\"title\":\"Pregunta eliminada por usuario?\",\"url\":\"https://reddit.com/del\"," +
                "\"author\":\"[deleted]\",\"selftext\":\"[deleted]\",\"num_comments\":20}}," +
                "{\"data\":{\"title\":\"Pregunta removida por moderador?\",\"url\":\"https://reddit.com/rem\"," +
                "\"author\":\"someUser\",\"selftext\":\"[removed]\",\"num_comments\":15}}" +
                "]}}";
        JsonNode mockBody = new ObjectMapper().readTree(json);

        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(mockBody));

        List<TopAnsweredQuestionDTO> result = collectorService.getMostAnsweredQuestionsOfWeek("learnprogramming");

        // Solo la pregunta válida debe pasar el filtro
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Pregunta válida?");
        assertThat(result.get(0).getAuthor()).isEqualTo("realUser");
        assertThat(result.get(0).getNumAnswers()).isEqualTo(30);
    }

    @Test
    void getMostAnsweredQuestions_conErrorDeRed_debeRetornarListaVacia() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenThrow(new RuntimeException("Timeout"));

        List<TopAnsweredQuestionDTO> result = collectorService.getMostAnsweredQuestionsOfWeek("learnprogramming");

        assertThat(result).isEmpty();
    }
}

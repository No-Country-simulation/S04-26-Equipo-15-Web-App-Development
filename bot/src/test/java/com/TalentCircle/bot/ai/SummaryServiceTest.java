package com.TalentCircle.bot.ai;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.ai.prompt.SummaryPromptBuilder;
import com.TalentCircle.bot.ai.service.SummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    private SummaryService summaryService;

    @Mock
    private SummaryPromptBuilder mockPromptBuilder;

    @Mock
    private ChatClient.Builder mockChatClientBuilder;

    @Mock
    private ChatClient mockChatClient;

    @BeforeEach
    void setUp() {
        // Configuramos el mock del Builder para retornar el ChatClient mockeado
        when(mockChatClientBuilder.build()).thenReturn(mockChatClient);
        summaryService = new SummaryService(mockChatClientBuilder, mockPromptBuilder);
    }

    @Test
    void rankTopContributions_debeLlamarAlLLMYMapearJSONCorrectamente() {
        // 1. Preparar datos de entrada
        List<WeeklyActivityDTO> activities = List.of(
                new WeeklyActivityDTO("Test Title", "http://test.com", "Reddit", 100, "author", "java", "content", 10)
        );

        // 2. Definir los mocks de la cadena de llamadas del ChatClient de Spring AI
        ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec mockResponseSpec = mock(ChatClient.CallResponseSpec.class);

        when(mockPromptBuilder.buildRankingPrompt(anyString(), anyString())).thenReturn("Prompt final renderizado");
        when(mockChatClient.prompt()).thenReturn(mockRequestSpec);
        when(mockRequestSpec.user(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.call()).thenReturn(mockResponseSpec);

        // Simulamos la respuesta JSON estructurada del LLM
        String mockJsonResponse = """
                [
                  {
                    "title": "Test Title",
                    "url": "http://test.com",
                    "author": "author",
                    "score": 100,
                    "numComments": 10,
                    "community": "java",
                    "justification": "Justificación de prueba"
                  }
                ]
                """;
        when(mockResponseSpec.content()).thenReturn(mockJsonResponse);

        // 3. Ejecución
        List<ContributionSummaryDTO> result = summaryService.rankTopContributions(activities);

        // 4. Verificación de resultados
        assertThat(result).hasSize(1);
        ContributionSummaryDTO summary = result.get(0);
        assertThat(summary.title()).isEqualTo("Test Title");
        assertThat(summary.score()).isEqualTo(100);
        assertThat(summary.justification()).isEqualTo("Justificación de prueba");
    }

    @Test
    void rankTopContributions_conListaVacia_debeRetornarListaVaciaDeInmediato() {
        List<ContributionSummaryDTO> result = summaryService.rankTopContributions(List.of());
        assertThat(result).isEmpty();
    }
}
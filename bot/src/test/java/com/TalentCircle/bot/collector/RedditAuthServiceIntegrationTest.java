package com.TalentCircle.bot.collector;

import com.TalentCircle.bot.collector.service.RedditAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Test de integración para RedditAuthService (SCRUM-14).
 *
 * Usa @RestClientTest para levantar solo el contexto del cliente HTTP sin necesitar
 * base de datos ni otras dependencias externas. MockRestServiceServer simula el
 * endpoint de Reddit, permitiendo verificar la autenticación de forma aislada.
 */
@RestClientTest(RedditAuthService.class)
@TestPropertySource(properties = {
        "reddit.client-id=test-client-id",
        "reddit.client-secret=test-client-secret",
        "reddit.user-agent=TestBot/1.0"
})
class RedditAuthServiceIntegrationTest {

    private static final String TOKEN_URL = "https://www.reddit.com/api/v1/access_token";

    /** Respuesta JSON de ejemplo que simula lo que devuelve Reddit al autenticar. */
    private static final String MOCK_TOKEN_RESPONSE =
            "{\"access_token\":\"mock-token-abc123\",\"token_type\":\"bearer\",\"expires_in\":3600,\"scope\":\"*\"}";

    @Autowired
    private RedditAuthService redditAuthService;

    /**
     * MockRestServiceServer intercepta las peticiones del RestTemplate de RedditAuthService.
     * @RestClientTest lo configura automáticamente al usar RestTemplateBuilder.
     */
    @Autowired
    private MockRestServiceServer mockServer;

    /**
     * Antes de cada test: limpia expectativas previas del mock server y fuerza
     * la invalidación del token en memoria para garantizar estado inicial limpio.
     */
    @BeforeEach
    void setUp() {
        mockServer.reset();
        redditAuthService.invalidateToken();
    }

    /**
     * Verifica que al pedir el token por primera vez:
     * - Se hace POST al endpoint correcto de Reddit
     * - Se envía HTTP Basic Auth (cabecera Authorization con "Basic ...")
     * - Se envía el User-Agent configurado
     * - El token devuelto coincide con el de la respuesta simulada
     */
    @Test
    void shouldObtainAccessTokenWithClientCredentials() {
        // Configura el mock para aceptar exactamente una petición al endpoint de tokens
        mockServer.expect(requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", startsWith("Basic")))
                .andExpect(header("User-Agent", "TestBot/1.0"))
                .andRespond(withSuccess(MOCK_TOKEN_RESPONSE, MediaType.APPLICATION_JSON));

        String token = redditAuthService.getAccessToken();

        assertThat(token).isEqualTo("mock-token-abc123");
        // Verifica que se hizo exactamente la petición configurada y ninguna más
        mockServer.verify();
    }

    /**
     * Verifica que el token se almacena en memoria: si no ha expirado,
     * una segunda llamada a getAccessToken() NO debe generar una nueva petición HTTP.
     */
    @Test
    void shouldCacheTokenAndReuseItWhenNotExpired() {
        // Solo se espera UNA petición HTTP, aunque getAccessToken() se llame dos veces
        mockServer.expect(requestTo(TOKEN_URL))
                .andRespond(withSuccess(MOCK_TOKEN_RESPONSE, MediaType.APPLICATION_JSON));

        String firstCall  = redditAuthService.getAccessToken();
        String secondCall = redditAuthService.getAccessToken(); // debe usar el token cacheado

        assertThat(firstCall).isEqualTo(secondCall).isEqualTo("mock-token-abc123");
        // Si se hubiera hecho una segunda petición, mockServer lanzaría AssertionError
        mockServer.verify();
    }

    /**
     * Verifica que si Reddit devuelve una respuesta sin "access_token",
     * el servicio lanza una excepción clara en lugar de retornar null.
     */
    @Test
    void shouldThrowExceptionWhenResponseHasNoAccessToken() {
        mockServer.expect(requestTo(TOKEN_URL))
                .andRespond(withSuccess("{\"error\":\"invalid_client\"}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> redditAuthService.getAccessToken())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to obtain Reddit access token");
    }
}

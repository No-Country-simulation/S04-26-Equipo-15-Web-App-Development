// =========================================================================
// SCRUM-14: Tests de integración para RedditAuthService
// Archivo creado como parte de SCRUM-14. Conservado como referencia.
// Para activarlo: descomentar el código y asegurarse de que RedditAuthService
// esté activo (descomentado en su archivo correspondiente).
// =========================================================================

/*
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

// Test de integración para RedditAuthService (SCRUM-14).
// Usa @RestClientTest para levantar solo el contexto del cliente HTTP sin necesitar
// base de datos ni otras dependencias externas. MockRestServiceServer simula el
// endpoint de Reddit, permitiendo verificar la autenticación de forma aislada.
@RestClientTest(RedditAuthService.class)
@TestPropertySource(properties = {
        "reddit.client-id=test-client-id",
        "reddit.client-secret=test-client-secret",
        "reddit.user-agent=TestBot/1.0"
})
class RedditAuthServiceIntegrationTest {

    private static final String TOKEN_URL = "https://www.reddit.com/api/v1/access_token";

    private static final String MOCK_TOKEN_RESPONSE =
            "{\"access_token\":\"mock-token-abc123\",\"token_type\":\"bearer\",\"expires_in\":3600,\"scope\":\"*\"}";

    @Autowired
    private RedditAuthService redditAuthService;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer.reset();
        redditAuthService.invalidateToken();
    }

    @Test
    void shouldObtainAccessTokenWithClientCredentials() {
        mockServer.expect(requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", startsWith("Basic")))
                .andExpect(header("User-Agent", "TestBot/1.0"))
                .andRespond(withSuccess(MOCK_TOKEN_RESPONSE, MediaType.APPLICATION_JSON));

        String token = redditAuthService.getAccessToken();

        assertThat(token).isEqualTo("mock-token-abc123");
        mockServer.verify();
    }

    @Test
    void shouldCacheTokenAndReuseItWhenNotExpired() {
        mockServer.expect(requestTo(TOKEN_URL))
                .andRespond(withSuccess(MOCK_TOKEN_RESPONSE, MediaType.APPLICATION_JSON));

        String firstCall  = redditAuthService.getAccessToken();
        String secondCall = redditAuthService.getAccessToken();

        assertThat(firstCall).isEqualTo(secondCall).isEqualTo("mock-token-abc123");
        mockServer.verify();
    }

    @Test
    void shouldThrowExceptionWhenResponseHasNoAccessToken() {
        mockServer.expect(requestTo(TOKEN_URL))
                .andRespond(withSuccess("{\"error\":\"invalid_client\"}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> redditAuthService.getAccessToken())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to obtain Reddit access token");
    }
}
*/

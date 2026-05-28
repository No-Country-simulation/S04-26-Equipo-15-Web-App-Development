// =========================================================================
// SCRUM-14: Servicio OAuth2 para autenticación con la Reddit API
// Archivo creado como parte de SCRUM-14. Conservado como referencia.
// Para activarlo: descomentar el código y agregar las dependencias
// en CollectorService y application.properties.
// =========================================================================

/*
package com.TalentCircle.bot.collector.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class RedditAuthService {

    // Endpoint de Reddit para obtener tokens de acceso OAuth2
    private static final String TOKEN_URL = "https://www.reddit.com/api/v1/access_token";

    // Client ID del app registrado en https://www.reddit.com/prefs/apps
    @Value("${reddit.client-id}")
    private String clientId;

    // Client Secret del app registrado en Reddit
    @Value("${reddit.client-secret}")
    private String clientSecret;

    // User-Agent requerido por Reddit para identificar la aplicación
    @Value("${reddit.user-agent}")
    private String userAgent;

    // RestTemplate construido desde RestTemplateBuilder para permitir mocking en tests
    private final RestTemplate restTemplate;

    // Token de acceso almacenado en memoria (se pierde al reiniciar la app)
    private String accessToken;

    // Marca de tiempo en la que el token expira; inicia en EPOCH para forzar fetch inicial
    private Instant tokenExpiresAt = Instant.EPOCH;

    public RedditAuthService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    // Devuelve un Bearer token válido.
    // Si el token en memoria no existe o ya expiró, solicita uno nuevo a Reddit.
    // Synchronized para evitar condiciones de carrera con múltiples hilos.
    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiresAt)) {
            fetchNewToken();
        }
        return accessToken;
    }

    // Solicita un nuevo token a la Reddit API usando el flujo client_credentials.
    // - Autenticación: HTTP Basic Auth con clientId:clientSecret (Base64).
    // - Cuerpo: grant_type=client_credentials (form-urlencoded).
    // - Se reservan 60 segundos de margen antes de la expiración real.
    private void fetchNewToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);
        headers.set("User-Agent", userAgent);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                TOKEN_URL,
                new HttpEntity<>(body, headers),
                JsonNode.class
        );

        JsonNode responseBody = response.getBody();
        if (responseBody == null || !responseBody.has("access_token")) {
            throw new RuntimeException("Failed to obtain Reddit access token");
        }

        accessToken = responseBody.get("access_token").asText();
        int expiresIn = responseBody.has("expires_in") ? responseBody.get("expires_in").asInt() : 3600;
        tokenExpiresAt = Instant.now().plusSeconds(expiresIn - 60);
    }

    // Invalida el token en memoria.
    // Solo debe usarse en tests para garantizar un estado limpio entre casos.
    public void invalidateToken() {
        accessToken = null;
        tokenExpiresAt = Instant.EPOCH;
    }
}
*/

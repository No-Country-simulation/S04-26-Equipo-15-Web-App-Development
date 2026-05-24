package com.TalentCircle.bot.collector.service;

import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectorService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<WeeklyActivityDTO> getTopWeeklyResourcesFromReddit(String subreddit) {
        String url = "https://www.reddit.com/r/" + subreddit + "/top.json?t=week";

        HttpHeaders headers = new HttpHeaders();
        // Custom User-Agent is required by Reddit to avoid Too Many Requests (429) errors
        headers.set("User-Agent", "TalentCircleBot/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<WeeklyActivityDTO> activities = new ArrayList<>();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode body = response.getBody();

            if (body != null && body.has("data") && body.get("data").has("children")) {
                JsonNode posts = body.get("data").get("children");
                for (JsonNode postNode : posts) {
                    JsonNode data = postNode.get("data");

                    String title = data.has("title") ? data.get("title").asText() : "";
                    String postUrl = data.has("url") ? data.get("url").asText() : "";
                    int score = data.has("score") ? data.get("score").asInt() : 0;
                    String author = data.has("author") ? data.get("author").asText() : "";
                    String comm = data.has("subreddit") ? data.get("subreddit").asText() : subreddit;

                    // Extraemos el contenido o cuerpo del post (en Reddit se llama 'selftext')
                    String content = data.has("selftext") ? data.get("selftext").asText() : "";

                    WeeklyActivityDTO activity = new WeeklyActivityDTO(
                            title, postUrl, "Reddit", score, author, comm, content
                    );
                    activities.add(activity);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching data from Reddit: " + e.getMessage());
        }

        return activities;
    }

    // =========================================================================
    // SCRUM-15: Obtención de posts top con num_comments y manejo de rate limits
    // =========================================================================

    // Delay inicial en ms entre reintentos por rate limit — se puede sobrescribir en tests con ReflectionTestUtils
    int scrum15RetryDelayMs = 1000;

    // Obtiene posts más votados de la semana incluyendo num_comments.
    // Reintenta hasta 3 veces con backoff exponencial si Reddit devuelve 429.
    public List<WeeklyActivityDTO> getTopWeeklyResourcesWithRateLimitHandling(String subreddit) {
        String url = "https://www.reddit.com/r/" + subreddit + "/top.json?t=week";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "TalentCircleBot/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<WeeklyActivityDTO> activities = new ArrayList<>();
        int maxRetries = 3;
        int currentDelay = scrum15RetryDelayMs;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
                JsonNode body = response.getBody();

                if (body != null && body.has("data") && body.get("data").has("children")) {
                    JsonNode posts = body.get("data").get("children");
                    for (JsonNode postNode : posts) {
                        JsonNode data = postNode.get("data");

                        String title       = data.has("title")        ? data.get("title").asText()       : "";
                        String postUrl     = data.has("url")           ? data.get("url").asText()          : "";
                        int    score       = data.has("score")         ? data.get("score").asInt()         : 0;
                        String author      = data.has("author")        ? data.get("author").asText()       : "";
                        String community   = data.has("subreddit")     ? data.get("subreddit").asText()    : subreddit;
                        String content     = data.has("selftext")      ? data.get("selftext").asText()     : "";
                        int    numComments = data.has("num_comments")  ? data.get("num_comments").asInt()  : 0;

                        activities.add(new WeeklyActivityDTO(title, postUrl, "Reddit", score, author, community, content, numComments));
                    }
                }
                break; // petición exitosa, salir del loop de reintentos

            } catch (org.springframework.web.client.HttpClientErrorException e) {
                if (e.getStatusCode().value() == 429) {
                    System.err.println("Rate limit (429). Intento " + attempt + "/" + maxRetries);
                    if (attempt < maxRetries && currentDelay > 0) {
                        try { Thread.sleep(currentDelay); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                        currentDelay *= 2;
                    }
                } else {
                    System.err.println("HTTP error " + e.getStatusCode() + ": " + e.getMessage());
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error fetching Reddit: " + e.getMessage());
                break;
            }
        }
        return activities;
    }

    // =========================================================================
    // SCRUM-14: Autenticación OAuth2 con Reddit API
    // Código implementado como parte de SCRUM-14. Conservado como referencia.
    // =========================================================================

    /*
    private final RedditAuthService redditAuthService;

    public CollectorService(RedditAuthService redditAuthService) {
        this.redditAuthService = redditAuthService;
    }

    // Obtiene los posts más votados de la semana usando autenticación OAuth2.
    // Envía un Bearer token y llama al endpoint oficial de Reddit (oauth.reddit.com)
    // en lugar de la capa JSON pública sin autenticar (www.reddit.com/r/.../.json).
    //
    // @param subreddit nombre del subreddit sin prefijo "r/" (ej: "java")
    // @return lista de actividades semanales; lista vacía si ocurre un error
    public List<WeeklyActivityDTO> getTopWeeklyResourcesOAuth(String subreddit) {
        // Las peticiones autenticadas deben ir a oauth.reddit.com, no a www.reddit.com
        String url = "https://oauth.reddit.com/r/" + subreddit + "/top?t=week";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "TalentCircleBot/1.0");
        // Bearer token OAuth2: RedditAuthService lo renueva automáticamente al expirar
        headers.setBearerAuth(redditAuthService.getAccessToken());

        List<WeeklyActivityDTO> activities = new ArrayList<>();

        try {
            ResponseEntity<com.fasterxml.jackson.databind.JsonNode> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), com.fasterxml.jackson.databind.JsonNode.class);
            com.fasterxml.jackson.databind.JsonNode body = response.getBody();

            if (body != null && body.has("data") && body.get("data").has("children")) {
                com.fasterxml.jackson.databind.JsonNode posts = body.get("data").get("children");
                for (com.fasterxml.jackson.databind.JsonNode postNode : posts) {
                    com.fasterxml.jackson.databind.JsonNode data = postNode.get("data");

                    String title   = data.has("title")    ? data.get("title").asText()    : "";
                    String postUrl = data.has("url")       ? data.get("url").asText()       : "";
                    int    score   = data.has("score")     ? data.get("score").asInt()      : 0;
                    String author  = data.has("author")    ? data.get("author").asText()    : "";
                    String comm    = data.has("subreddit") ? data.get("subreddit").asText() : subreddit;
                    // selftext es el cuerpo del post; vacío en posts de solo enlace
                    String content = data.has("selftext")  ? data.get("selftext").asText()  : "";

                    activities.add(new WeeklyActivityDTO(title, postUrl, "Reddit", score, author, comm, content));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching authenticated data from Reddit: " + e.getMessage());
        }

        return activities;
    }
    */
}

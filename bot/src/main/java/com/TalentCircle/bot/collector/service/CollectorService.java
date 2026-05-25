package com.TalentCircle.bot.collector.service;

import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.collector.dto.TopAnsweredQuestionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
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



    // Delay inicial en ms entre reintentos por rate limit — se puede sobrescribir en tests con ReflectionTestUtils
    int RetryDelayMs = 1000;

    // Obtiene posts más votados de la semana incluyendo num_comments.
    // Reintenta hasta 3 veces con backoff exponencial si Reddit devuelve 429.
    public List<WeeklyActivityDTO> getTopWeeklyResourcesWithRateLimitHandling(String subreddit) {
        String url = "https://www.reddit.com/r/" + subreddit + "/top.json?t=week";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "TalentCircleBot/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<WeeklyActivityDTO> activities = new ArrayList<>();
        int maxRetries = 3;
        int currentDelay = RetryDelayMs;

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

    // Detecta si un post fue eliminado por el usuario o removido por moderadores.
    private boolean isDeletedOrModerated(JsonNode data) {
        String author   = data.has("author")   ? data.get("author").asText()   : "";
        String selftext = data.has("selftext")  ? data.get("selftext").asText() : "";
        return "[deleted]".equals(author)
                || "[removed]".equals(selftext)
                || "[deleted]".equals(selftext);
    }

    // Retorna las preguntas (posts con "?" en el título) más respondidas de la semana,
    // ordenadas de mayor a menor número de comentarios.
    public List<TopAnsweredQuestionDTO> getMostAnsweredQuestionsOfWeek(String subreddit) {
        String url = "https://www.reddit.com/r/" + subreddit + "/top.json?t=week";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "TalentCircleBot/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<TopAnsweredQuestionDTO> questions = new ArrayList<>();

        try {
            ResponseEntity<JsonNode> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode body = response.getBody();

            if (body != null && body.has("data") && body.get("data").has("children")) {
                for (JsonNode postNode : body.get("data").get("children")) {
                    JsonNode data = postNode.get("data");

                    String title  = data.has("title")  ? data.get("title").asText()  : "";
                    String author = data.has("author") ? data.get("author").asText() : "";

                    // Solo posts tipo pregunta y que no estén eliminados o moderados
                    if (!title.contains("?") || isDeletedOrModerated(data)) {
                        continue;
                    }

                    String postUrl    = data.has("url")          ? data.get("url").asText()          : "";
                    int    numAnswers = data.has("num_comments")  ? data.get("num_comments").asInt()  : 0;

                    questions.add(new TopAnsweredQuestionDTO(title, postUrl, numAnswers, author));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching questions from Reddit: " + e.getMessage());
        }

        questions.sort(Comparator.comparingInt(TopAnsweredQuestionDTO::getNumAnswers).reversed());
        return questions;
    }


    /*
    private final RedditAuthService redditAuthService;

    public CollectorService(RedditAuthService redditAuthService) {
        this.redditAuthService = redditAuthService;
    }

    public List<WeeklyActivityDTO> getTopWeeklyResourcesOAuth(String subreddit) {
        String url = "https://oauth.reddit.com/r/" + subreddit + "/top?t=week";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "TalentCircleBot/1.0");
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

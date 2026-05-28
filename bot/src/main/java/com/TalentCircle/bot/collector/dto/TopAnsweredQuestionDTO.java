// =========================================================================
// SCRUM-16: DTO para preguntas más respondidas de la semana
// Retorna los campos mínimos requeridos: título, URL, respuestas y autor.
// =========================================================================

package com.TalentCircle.bot.collector.dto;

public class TopAnsweredQuestionDTO {

    private String title;
    private String url;
    private int numAnswers;
    private String author;

    public TopAnsweredQuestionDTO() {}

    public TopAnsweredQuestionDTO(String title, String url, int numAnswers, String author) {
        this.title = title;
        this.url = url;
        this.numAnswers = numAnswers;
        this.author = author;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getNumAnswers() { return numAnswers; }
    public void setNumAnswers(int numAnswers) { this.numAnswers = numAnswers; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}

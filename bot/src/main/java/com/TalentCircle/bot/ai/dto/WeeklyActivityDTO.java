package com.TalentCircle.bot.ai.dto;

public class WeeklyActivityDTO {
    private String title;
    private String url;
    private String source;
    private int score;
    private String author;
    private String community;
    private String content; // Nuevo campo para el contenido del post

    public WeeklyActivityDTO() {
    }

    public WeeklyActivityDTO(String title, String url, String source, int score, String author, String community, String content) {
        this.title = title;
        this.url = url;
        this.source = source;
        this.score = score;
        this.author = author;
        this.community = community;
        this.content = content;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCommunity() { return community; }
    public void setCommunity(String community) { this.community = community; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}


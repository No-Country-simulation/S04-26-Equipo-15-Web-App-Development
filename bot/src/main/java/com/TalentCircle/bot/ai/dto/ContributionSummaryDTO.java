package com.TalentCircle.bot.ai.dto;

public class ContributionSummaryDTO {

    private String title;
    private String summary;
    private String url;

    public ContributionSummaryDTO() {
    }

    public ContributionSummaryDTO(String title, String summary, String url) {
        this.title = title;
        this.summary = summary;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
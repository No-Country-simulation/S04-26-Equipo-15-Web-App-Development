package com.TalentCircle.bot.ai.dto;

public record WeeklyActivityDTO(
        String title,
        String url,
        String source,
        int score,
        String author,
        String community,
        String content
) {
}
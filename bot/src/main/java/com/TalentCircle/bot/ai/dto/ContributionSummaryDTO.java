package com.TalentCircle.bot.ai.dto;

public record ContributionSummaryDTO(
        String title,
        String url,
        String author,
        int score,
        int numComments,
        String community,
        String justification
) {}
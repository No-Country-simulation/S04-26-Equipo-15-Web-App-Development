package com.TalentCircle.bot.ai.dto;

import java.util.List;

public record WeeklyActivityDTO(
        List<ContributionSummaryDTO> contributions
) {
}
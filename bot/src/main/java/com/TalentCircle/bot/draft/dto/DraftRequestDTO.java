package com.TalentCircle.bot.draft.dto;

import java.util.List;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;

public record DraftRequestDTO(
        List<ContributionSummaryDTO> summaries
) {
}
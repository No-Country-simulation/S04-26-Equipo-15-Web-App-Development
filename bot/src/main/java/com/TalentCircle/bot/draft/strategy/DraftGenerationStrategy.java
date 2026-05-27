package com.TalentCircle.bot.draft.strategy;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import java.util.List;

public interface DraftGenerationStrategy {
    DraftChannel getChannel();

    String generateDraft(List<ContributionSummaryDTO> topContributions);
}
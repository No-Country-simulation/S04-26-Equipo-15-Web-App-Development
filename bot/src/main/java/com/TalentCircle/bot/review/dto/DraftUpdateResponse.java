package com.TalentCircle.bot.review.dto;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;

import java.time.LocalDateTime;

public record DraftUpdateResponse(
        Long id,
        DraftChannel channel,
        DraftStatus status,
        String content,
        LocalDateTime updatedAt
) {}

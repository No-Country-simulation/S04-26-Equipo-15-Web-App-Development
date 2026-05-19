package com.TalentCircle.bot.review.dto;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;

import java.time.LocalDateTime;

public record DraftResponse(
        Long id,
        DraftChannel channel,
        DraftStatus status,
        String preview,
        LocalDateTime createdAt
) {}

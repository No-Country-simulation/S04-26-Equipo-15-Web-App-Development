package com.TalentCircle.bot.publisher.dto;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;

import java.time.LocalDateTime;

public record DraftExportResponse(
        Long id,
        DraftChannel channel,
        DraftStatus status,
        String content,
        LocalDateTime approvedAt
) {}

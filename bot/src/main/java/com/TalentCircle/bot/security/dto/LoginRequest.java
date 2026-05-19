package com.TalentCircle.bot.security.dto;

public record LoginRequest(
        String email,
        String password
) {}

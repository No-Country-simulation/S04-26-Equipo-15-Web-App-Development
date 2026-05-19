package com.TalentCircle.bot.ai.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.TalentCircle.bot.ai.dto.*;

@Service
public class SummaryService {

    private final ChatClient chatClient;

    public SummaryService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ContributionSummaryDTO generateSummary(
            ContributionSummaryDTO contribution
    ) {

        String prompt = """
                You are an editorial assistant for TalentCircle.

                Generate a concise summary in a maximum of 3 sentences.

                Include:
                - Main topic
                - Why it matters for TalentCircle
                - Original source link
                """;

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return new ContributionSummaryDTO(
                contribution.title(),
                response,
                contribution.originalUrl()
        );
    }

    public List<ContributionSummaryDTO> generateWeeklySummaries(
            WeeklyActivityDTO weeklyActivity
    ) {

        return weeklyActivity.contributions()
                .stream()
                .map(this::generateSummary)
                .toList();
    }
}
package com.TalentCircle.bot.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.TalentCircle.bot.ai.dto.*;
import com.TalentCircle.bot.ai.exceptions.SummaryGenerationException;

@Service
public class SummaryService {

        private final ChatClient chatClient;

        public SummaryService(ChatClient.Builder chatClientBuilder) {
                this.chatClient = chatClientBuilder.build();
        }

        public ContributionSummaryDTO generateSummary(WeeklyActivityDTO activity) {

                try {

                        String prompt = """
                                        Summarize the following contribution
                                        in a maximum of 3 sentences.

                                        Include:
                                        - Main topic
                                        - Why it is relevant for TalentCircle
                                        - Original link

                                        Contribution:
                                        Title: %s
                                        Source: %s
                                        Community: %s
                                        Author: %s
                                        Score: %d
                                        Content: %s
                                        Link: %s
                                        """
                                        .formatted(
                                                        activity.title(),
                                                        activity.source(),
                                                        activity.community(),
                                                        activity.author(),
                                                        activity.score(),
                                                        activity.content(),
                                                        activity.url());

                        String summary = chatClient.prompt()
                                        .user(prompt)
                                        .call()
                                        .content();

                        return new ContributionSummaryDTO(
                                        activity.title(),
                                        summary,
                                        activity.url());

                } catch (Exception ex) {

                        throw new SummaryGenerationException(
                                        "Failed to generate contribution summary",
                                        ex);
                }
        }
}
package com.TalentCircle.bot.ai.service;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.TalentCircle.bot.ai.exceptions.SummaryGenerationException;
import java.util.List;

@Service
public class SummaryService {

        private final ChatClient chatClient;

        public SummaryService(ChatClient.Builder chatClientBuilder) {
                this.chatClient = chatClientBuilder.build();
        }

    public ContributionSummaryDTO generateSummary(
            WeeklyActivityDTO activity
    ) {

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
                                activity.getTitle(),
                                activity.getSource(),
                                activity.getCommunity(),
                                activity.getAuthor(),
                                activity.getScore(),
                                activity.getContent(),
                                activity.getUrl()
                    );

            String summary = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return new ContributionSummaryDTO(
        activity.getTitle(),
        activity.getUrl(),
        activity.getAuthor(),
        activity.getScore(),
        activity.getNumComments(),
        activity.getCommunity(),
        summary
);

        } catch (Exception ex) {

            throw new SummaryGenerationException(
                    "Failed to generate contribution summary",
                    ex);
        }
    }

        public List<ContributionSummaryDTO> rankTopContributions(
                        List<WeeklyActivityDTO> activities) {

                return activities.stream()
                                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                                .limit(5)
                                .map(this::generateSummary)
                                .toList();
        }
}
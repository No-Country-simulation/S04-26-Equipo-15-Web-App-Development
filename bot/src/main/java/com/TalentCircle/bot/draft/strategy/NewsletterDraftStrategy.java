package com.TalentCircle.bot.draft.strategy;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.draft.prompt.DraftPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsletterDraftStrategy implements DraftGenerationStrategy {

    private final ChatClient chatClient;
    private final DraftPromptBuilder promptBuilder;

    public NewsletterDraftStrategy(ChatClient.Builder builder, DraftPromptBuilder promptBuilder) {
        this.chatClient = builder.build();
        this.promptBuilder = promptBuilder;
    }

    @Override
    public DraftChannel getChannel() {
        return DraftChannel.NEWSLETTER;
    }

    @Override
    public String generateDraft(List<ContributionSummaryDTO> topContributions) {
        String formatted = formatContributions(topContributions);
        String prompt = promptBuilder.buildNewsletterPrompt(formatted);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String formatContributions(List<ContributionSummaryDTO> contributions) {
        StringBuilder sb = new StringBuilder();
        for (ContributionSummaryDTO c : contributions) {
            sb.append("### ").append(c.title()).append("\n");
            sb.append("**Autor:** ").append(c.author()).append(" | **Subreddit:** /r/").append(c.community()).append("\n\n");
            sb.append(c.justification()).append("\n\n");
            if (c.url() != null && !c.url().isBlank()) {
                sb.append("Enlace al post: [Ver en Reddit](").append(c.url()).append(")\n\n");
            }
            sb.append("---\n\n");
        }
        return sb.toString();
    }
}
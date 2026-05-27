package com.TalentCircle.bot.draft.strategy;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.draft.prompt.DraftPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TwitterDraftStrategy implements DraftGenerationStrategy {

    private final ChatClient chatClient;
    private final DraftPromptBuilder promptBuilder;

    public TwitterDraftStrategy(ChatClient.Builder builder, DraftPromptBuilder promptBuilder) {
        this.chatClient = builder.build();
        this.promptBuilder = promptBuilder;
    }

    @Override
    public DraftChannel getChannel() {
        return DraftChannel.TWITTER;
    }

    @Override
    public String generateDraft(List<ContributionSummaryDTO> topContributions) {
        String formatted = formatContributions(topContributions);
        String prompt = promptBuilder.buildTwitterPrompt(formatted);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String formatContributions(List<ContributionSummaryDTO> contributions) {
        StringBuilder sb = new StringBuilder();
        for (ContributionSummaryDTO c : contributions) {
            sb.append("- ").append(c.title()).append("\n");
        }
        return sb.toString();
    }
}
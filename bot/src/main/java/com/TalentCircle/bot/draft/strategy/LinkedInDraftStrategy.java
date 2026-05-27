package com.TalentCircle.bot.draft.strategy;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.draft.prompt.DraftPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkedInDraftStrategy implements DraftGenerationStrategy {

    private final ChatClient chatClient;
    private final DraftPromptBuilder promptBuilder;

    // Inyección por constructor
    public LinkedInDraftStrategy(ChatClient.Builder builder, DraftPromptBuilder promptBuilder) {
        this.chatClient = builder.build();
        this.promptBuilder = promptBuilder;
    }

    @Override
    public DraftChannel getChannel() {
        return DraftChannel.LINKEDIN;
    }

    @Override
    public String generateDraft(List<ContributionSummaryDTO> topContributions) {
        // 1. Formateamos las contribuciones seleccionadas a texto
        String formatted = formatContributions(topContributions);

        // 2. Construimos el prompt usando la plantilla de LinkedIn
        String prompt = promptBuilder.buildLinkedinPrompt(formatted);

        // 3. Llamamos al LLM para obtener la redacción del post
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String formatContributions(List<ContributionSummaryDTO> contributions) {
        StringBuilder sb = new StringBuilder();
        for (ContributionSummaryDTO c : contributions) {
            sb.append("- ").append(c.title()).append(" (por @").append(c.author()).append(")\n");
            sb.append("  Detalle: ").append(c.justification()).append("\n");
            if (c.url() != null && !c.url().isBlank()) {
                sb.append("  Enlace: ").append(c.url()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
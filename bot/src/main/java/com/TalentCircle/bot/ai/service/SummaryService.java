package com.TalentCircle.bot.ai.service;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.ai.prompt.SummaryPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    private final ChatClient chatClient;
    private final SummaryPromptBuilder promptBuilder;

    public SummaryService(ChatClient.Builder builder, SummaryPromptBuilder promptBuilder) {
        this.chatClient = builder.build();
        this.promptBuilder = promptBuilder;
    }
    public String testPrompt() {
        return chatClient.prompt()
                .user("Say hello from TalentCircle AI")
                .call()
                .content();
    }
    public List<ContributionSummaryDTO> rankTopContributions(List<WeeklyActivityDTO> activities) {
        if (activities == null || activities.isEmpty()) {
            return List.of();
        }

        String formattedActivities = formatActivitiesForPrompt(activities);

        var outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<ContributionSummaryDTO>>() {}
        );
        String formatInstructions = outputConverter.getFormat();

        String finalPrompt = promptBuilder.buildRankingPrompt(formattedActivities, formatInstructions);
        try {
            String responseContent = chatClient.prompt()
                    .user(finalPrompt)
                    .call()
                    .content();
            return outputConverter.convert(responseContent);
        } catch (Exception e) {
            System.err.println("Error al clasificar actividades con IA: " + e.getMessage());
            return List.of();
        }
    }
    private String formatActivitiesForPrompt(List<WeeklyActivityDTO> activities) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < activities.size(); i++) {
            WeeklyActivityDTO act = activities.get(i);
            sb.append(String.format("ID: %d\n", i));
            sb.append(String.format("Título: %s\n", act.getTitle()));
            sb.append(String.format("Subreddit: %s\n", act.getCommunity()));
            sb.append(String.format("Puntuación: %d | Comentarios: %d\n", act.getScore(), act.getNumComments()));
            if (act.getContent() != null && !act.getContent().isBlank()) {
                String cleanContent = act.getContent().length() > 300
                        ? act.getContent().substring(0, 300) + "..."
                        : act.getContent();
                sb.append(String.format("Contenido: %s\n", cleanContent));
            }
            sb.append("--------------------------------------------------\n");
        }
        return sb.toString();
    }
}
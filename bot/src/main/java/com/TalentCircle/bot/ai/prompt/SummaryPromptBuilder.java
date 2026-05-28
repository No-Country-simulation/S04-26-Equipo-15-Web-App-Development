package com.TalentCircle.bot.ai.prompt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SummaryPromptBuilder {

    @Value("classpath:prompts/ranking-prompt.st")
    private Resource rankingPromptResource;

    public String buildRankingPrompt(String formattedActivities, String formatInstructions) {
        PromptTemplate template = new PromptTemplate(rankingPromptResource);
        return template.render(Map.of(
                "activities", formattedActivities,
                "format_instructions", formatInstructions
        ));
    }
}
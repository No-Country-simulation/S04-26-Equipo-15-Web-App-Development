package com.TalentCircle.bot.draft.prompt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DraftPromptBuilder {

    @Value("classpath:prompts/linkedin-prompt.st")
    private Resource linkedinPromptResource;

    @Value("classpath:prompts/twitter-prompt.st")
    private Resource twitterPromptResource;

    @Value("classpath:prompts/newsletter-prompt.st")
    private Resource newsletterPromptResource;

    /**
     * Renderiza el prompt para LinkedIn con las actividades seleccionadas.
     */
    public String buildLinkedinPrompt(String formattedActivities) {
        return new PromptTemplate(linkedinPromptResource)
                .render(Map.of("activities", formattedActivities));
    }

    /**
     * Renderiza el prompt para Twitter/X con las actividades seleccionadas.
     */
    public String buildTwitterPrompt(String formattedActivities) {
        return new PromptTemplate(twitterPromptResource)
                .render(Map.of("activities", formattedActivities));
    }

    /**
     * Renderiza el prompt para el Newsletter con las actividades seleccionadas.
     */
    public String buildNewsletterPrompt(String formattedActivities) {
        return new PromptTemplate(newsletterPromptResource)
                .render(Map.of("activities", formattedActivities));
    }
}
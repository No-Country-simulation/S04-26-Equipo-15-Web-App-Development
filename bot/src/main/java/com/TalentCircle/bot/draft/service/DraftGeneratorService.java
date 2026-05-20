package com.TalentCircle.bot.draft.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.TalentCircle.bot.draft.dto.DraftRequestDTO;
import com.TalentCircle.bot.draft.dto.DraftResponseDTO;
import com.TalentCircle.bot.draft.exceptions.DraftGeneratorException;

@Service
public class DraftGeneratorService {

    private final ChatClient chatClient;

    public DraftGeneratorService(
            ChatClient.Builder chatClientBuilder
    ) {
        this.chatClient = chatClientBuilder.build();
    }

    public DraftResponseDTO generateDraft(
            DraftRequestDTO request
    ) {

        String summariesText = request.summaries()
                .stream()
                .map(summary -> """
                        Title: %s
                        Summary: %s
                        Link: %s
                        """
                        .formatted(
                                summary.title(),
                                summary.summary(),
                                summary.url()
                        )
                )
                .reduce("", String::concat);

        String prompt = """
                Generate a professional editorial-style newsletter draft.

                Requirements:
                - Between 400 and 600 words
                - Include:
                  1. Introduction
                  2. One section per contribution
                  3. Closing section
                - Include original links
                - Tone: editorial, professional, concise

                Contributions:
                %s
                """
                .formatted(summariesText);

        try {

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return new DraftResponseDTO(response);

        } catch (Exception e) {

            throw new DraftGeneratorException(
                    "Failed to generate newsletter draft"
            );
        }
    }
}

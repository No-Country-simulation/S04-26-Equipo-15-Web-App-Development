package com.TalentCircle.bot.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {

    private final ChatClient chatClient;

    public SummaryService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String testPrompt() {

        return chatClient.prompt()
                .user("Say hello from TalentCircle AI")
                .call()
                .content();
    }
}
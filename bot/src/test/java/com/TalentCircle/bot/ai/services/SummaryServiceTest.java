package com.TalentCircle.bot.ai.services;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.ai.service.SummaryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private SummaryService summaryService;

    @BeforeEach
    void setUp() {

        when(chatClientBuilder.build()).thenReturn(chatClient);

        summaryService = new SummaryService(chatClientBuilder);
    }

    @Test
    void shouldGenerateContributionSummary() {

        WeeklyActivityDTO activity = new WeeklyActivityDTO(
                "Spring AI released",
                "https://example.com",
                "Reddit",
                120,
                "juan",
                "r/java",
                "Spring AI now supports new models"
        );

        String mockedSummary =
                "Spring AI introduced support for new AI models. " +
                "This is relevant for TalentCircle because it improves AI integrations. " +
                "Original link: https://example.com";

        when(chatClient.prompt()).thenReturn(requestSpec);

        when(requestSpec.user(anyString())).thenReturn(requestSpec);

        when(requestSpec.call()).thenReturn(responseSpec);

        when(responseSpec.content()).thenReturn(mockedSummary);

        ContributionSummaryDTO result =
                summaryService.generateSummary(activity);

        assertNotNull(result);

        assertEquals(
                "Spring AI released",
                result.title()
        );

        assertEquals(
                mockedSummary,
                result.summary()
        );

        assertEquals(
                "https://example.com",
                result.url()
        );
    }
}
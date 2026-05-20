package com.TalentCircle.bot.ai.services;

import com.TalentCircle.bot.ai.dto.*;
import com.TalentCircle.bot.draft.dto.*;
import com.TalentCircle.bot.draft.service.DraftGeneratorService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DraftGeneratorServiceTest {

        @Mock
        private ChatClient.Builder chatClientBuilder;

        @Mock
        private ChatClient chatClient;

        @Mock
        private ChatClient.ChatClientRequestSpec requestSpec;

        @Mock
        private ChatClient.CallResponseSpec responseSpec;

        private DraftGeneratorService draftGeneratorService;

        @BeforeEach
        void setUp() {

                when(chatClientBuilder.build())
                                .thenReturn(chatClient);

                draftGeneratorService = new DraftGeneratorService(chatClientBuilder);
        }

        @Test
        void shouldGenerateNewsletterDraft() {

                List<ContributionSummaryDTO> summaries = List.of(
                                new ContributionSummaryDTO(
                                                "Spring AI",
                                                "Spring AI added support for new models.",
                                                "https://example.com/1"),
                                new ContributionSummaryDTO(
                                                "Java 21",
                                                "Java 21 improves virtual threads.",
                                                "https://example.com/2"));

                DraftRequestDTO request = new DraftRequestDTO(summaries);

                String mockedDraft = "This week in TalentCircle...";

                when(chatClient.prompt())
                                .thenReturn(requestSpec);

                when(requestSpec.user(anyString()))
                                .thenReturn(requestSpec);

                when(requestSpec.call())
                                .thenReturn(responseSpec);

                when(responseSpec.content())
                                .thenReturn(mockedDraft);

                DraftResponseDTO result = draftGeneratorService.generateDraft(request);

                assertNotNull(result);

                assertEquals(mockedDraft, result.content());

                verify(chatClient, times(1)).prompt();
        }
}
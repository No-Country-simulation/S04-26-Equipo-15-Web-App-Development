package com.TalentCircle.bot.ai;

import com.TalentCircle.bot.ai.service.SummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GROQ_API_KEY", matches = ".+")
class AiSmokeTest {

    @Autowired
    private SummaryService summaryService;

    @Test
    void chatClient_debeResponderAlModelo() {
        String response = summaryService.testPrompt();

        assertThat(response).isNotNull().isNotBlank();
    }
}

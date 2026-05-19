package com.TalentCircle.bot.ai.controller;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.ai.service.SummaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name= "SummaryController" , description = "Controlador para generar resúmenes de contribuciones")
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @Operation(summary = "Genera resúmenes para una lista de contribuciones semanales")
    @PostMapping("/summaries")
    public List<ContributionSummaryDTO> generateSummaries(
            @RequestBody WeeklyActivityDTO weeklyActivity
    ) {

        return summaryService.generateWeeklySummaries(weeklyActivity);
    }
}
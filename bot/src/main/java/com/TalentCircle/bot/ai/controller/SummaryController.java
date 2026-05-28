package com.TalentCircle.bot.ai.controller;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.ai.service.SummaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@Tag(name= "SummaryController" , description = "Controlador para generar resúmenes de contribuciones")
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    
    @Operation(summary = "Genera resúmenes para una lista de contribuciones semanales")
    @PostMapping("/summaries")
    public List<ContributionSummaryDTO> generateSummaries(
            @RequestBody WeeklyActivityDTO weeklyActivity
    ) {

        return List.of(summaryService.generateSummary(weeklyActivity));
    }

    @PostMapping("/rank")
    public List<ContributionSummaryDTO> rankActivities(@RequestBody List<WeeklyActivityDTO> activities) {
        return summaryService.rankTopContributions(activities);
    }
}
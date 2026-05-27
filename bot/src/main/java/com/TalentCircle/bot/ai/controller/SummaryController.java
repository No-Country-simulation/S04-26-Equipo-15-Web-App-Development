package com.TalentCircle.bot.ai.controller;

import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.ai.service.SummaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class SummaryController {

    private final SummaryService summaryService;

    // Inyección de dependencias por constructor
    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/test")
    public String test() {
        return summaryService.testPrompt();
    }

    @PostMapping("/rank")
    public List<ContributionSummaryDTO> rankActivities(@RequestBody List<WeeklyActivityDTO> activities) {
        return summaryService.rankTopContributions(activities);
    }
}
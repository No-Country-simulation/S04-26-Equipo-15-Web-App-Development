package com.TalentCircle.bot.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TalentCircle.bot.ai.service.SummaryService;

@RestController
@RequestMapping("/api/ai")
public class SummaryController {
    
    @Autowired
    private SummaryService summaryService;

    @GetMapping("/test")
    public String test() {
        return summaryService.testPrompt();
    }

}

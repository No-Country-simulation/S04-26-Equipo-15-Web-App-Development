package com.TalentCircle.bot.collector.controller;

import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.collector.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/collector")
public class CollectorController {

    @Autowired
    private CollectorService collectorService;

    @GetMapping("/reddit/{subreddit}")
    public List<WeeklyActivityDTO> getTopWeeklyReddit(@PathVariable String subreddit) {
        return collectorService.getTopWeeklyResourcesFromReddit(subreddit);
    }
}

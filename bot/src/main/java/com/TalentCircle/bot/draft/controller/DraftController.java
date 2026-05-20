package com.TalentCircle.bot.draft.controller;

import com.TalentCircle.bot.draft.dto.*;
import com.TalentCircle.bot.draft.service.DraftGeneratorService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/drafts")
public class DraftController {

    private final DraftGeneratorService draftGeneratorService;

    public DraftController(
            DraftGeneratorService draftGeneratorService
    ) {
        this.draftGeneratorService = draftGeneratorService;
    }

    @PostMapping
    public DraftResponseDTO generateDraft(
            @RequestBody DraftRequestDTO request
    ) {

        return draftGeneratorService.generateDraft(request);
    }
}
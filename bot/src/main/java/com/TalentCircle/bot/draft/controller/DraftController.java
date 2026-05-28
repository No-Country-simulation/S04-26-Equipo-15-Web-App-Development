package com.TalentCircle.bot.draft.controller;

<<<<<<< HEAD
import com.TalentCircle.bot.draft.dto.*;
import com.TalentCircle.bot.draft.service.DraftGeneratorService;
=======
import com.TalentCircle.bot.Entity.Draft;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.draft.service.DraftGeneratorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
>>>>>>> origin/main

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/drafts")
public class DraftController {

    private final DraftGeneratorService draftGeneratorService;

<<<<<<< HEAD
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
=======
    // Inyección por constructor
    public DraftController(DraftGeneratorService draftGeneratorService) {
        this.draftGeneratorService = draftGeneratorService;
    }

    /**
     * Endpoint para generar automáticamente los borradores de redes sociales
     * a partir de las actividades recopiladas de la semana.
     */
    @PostMapping("/generate")
    public List<Draft> generateDrafts(@RequestBody List<WeeklyActivityDTO> rawActivities) {
        return draftGeneratorService.generatePipelineDrafts(rawActivities);
>>>>>>> origin/main
    }
}
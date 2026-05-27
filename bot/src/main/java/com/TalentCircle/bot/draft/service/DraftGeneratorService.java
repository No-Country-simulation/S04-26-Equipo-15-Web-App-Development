package com.TalentCircle.bot.draft.service;

import com.TalentCircle.bot.Entity.Draft;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.Entity.PipelineRun;
import com.TalentCircle.bot.Entity.PipelineRunStatus;
import com.TalentCircle.bot.Repository.DraftRepository;
import com.TalentCircle.bot.ai.dto.ContributionSummaryDTO;
import com.TalentCircle.bot.ai.dto.WeeklyActivityDTO;
import com.TalentCircle.bot.ai.service.SummaryService;
import com.TalentCircle.bot.draft.repository.PipelineRunRepository;
import com.TalentCircle.bot.draft.strategy.DraftGenerationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DraftGeneratorService {

    private final SummaryService summaryService;
    private final PipelineRunRepository pipelineRunRepository;
    private final DraftRepository draftRepository;
    private final List<DraftGenerationStrategy> strategies;

    // Inyección de dependencias por constructor
    public DraftGeneratorService(
            SummaryService summaryService,
            PipelineRunRepository pipelineRunRepository,
            DraftRepository draftRepository,
            List<DraftGenerationStrategy> strategies) {
        this.summaryService = summaryService;
        this.pipelineRunRepository = pipelineRunRepository;
        this.draftRepository = draftRepository;
        this.strategies = strategies;
    }

    /**
     * Inicia el pipeline, filtra los posts con IA, genera los borradores
     * por cada canal y los almacena en base de datos.
     */
    @Transactional
    public List<Draft> generatePipelineDrafts(List<WeeklyActivityDTO> rawActivities) {
        if (rawActivities == null || rawActivities.isEmpty()) {
            return List.of();
        }

        // 1. Inicializar y guardar la ejecución del Pipeline
        PipelineRun pipelineRun = new PipelineRun();
        pipelineRun.setStatus(PipelineRunStatus.IN_PROGRESS);
        pipelineRun.setStartedAt(LocalDateTime.now());
        pipelineRun = pipelineRunRepository.save(pipelineRun);

        // 2. Obtener el ranking de las 3-5 mejores contribuciones con el LLM
        List<ContributionSummaryDTO> topContributions = summaryService.rankTopContributions(rawActivities);

        if (topContributions.isEmpty()) {
            // Si el ranking falla o no hay contenido apto, marcamos la ejecución como fallida
            pipelineRun.setStatus(PipelineRunStatus.FAILED);
            pipelineRun.setFinishedAt(LocalDateTime.now());
            pipelineRunRepository.save(pipelineRun);
            return List.of();
        }

        // 3. Ejecutar dinámicamente cada estrategia por canal para generar borradores
        List<Draft> generatedDrafts = new ArrayList<>();
        for (DraftGenerationStrategy strategy : strategies) {
            try {
                // Generar contenido del borrador con el LLM específico
                String content = strategy.generateDraft(topContributions);

                // Crear y rellenar entidad de Borrador
                Draft draft = new Draft();
                draft.setChannel(strategy.getChannel());
                draft.setStatus(DraftStatus.PENDING_REVIEW);
                draft.setContent(content);
                draft.setCreatedAt(LocalDateTime.now());
                draft.setPipelineRun(pipelineRun);

                // Guardar en la base de datos
                Draft savedDraft = draftRepository.save(draft);
                generatedDrafts.add(savedDraft);

            } catch (Exception e) {
                System.err.println("Error al generar borrador para " + strategy.getChannel() + ": " + e.getMessage());
            }
        }

        // 4. Actualizar y finalizar la ejecución del Pipeline
        pipelineRun.setStatus(PipelineRunStatus.SUCCESS);
        pipelineRun.setFinishedAt(LocalDateTime.now());
        pipelineRun.setDraftsGenerated(generatedDrafts.size());
        pipelineRunRepository.save(pipelineRun);

        return generatedDrafts;
    }
}
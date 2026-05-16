package com.TalentCircle.bot.review;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.publisher.DraftExportService;
import com.TalentCircle.bot.publisher.dto.DraftExportResponse;
import com.TalentCircle.bot.review.dto.DraftResponse;
import com.TalentCircle.bot.review.dto.DraftUpdateRequest;
import com.TalentCircle.bot.review.dto.DraftUpdateResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drafts")
public class DraftController {

    private final DraftService draftService;
    private final DraftExportService draftExportService;

    public DraftController(DraftService draftService, DraftExportService draftExportService) {
        this.draftService = draftService;
        this.draftExportService = draftExportService;
    }

    // US-019: GET /api/drafts?status=PENDING_REVIEW
    @GetMapping
    public ResponseEntity<Map<DraftChannel, List<DraftResponse>>> listDrafts(
            @RequestParam(defaultValue = "PENDING_REVIEW") DraftStatus status) {
        return ResponseEntity.ok(draftService.listByStatus(status));
    }

    // US-020: PUT /api/drafts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<DraftUpdateResponse> editDraft(
            @PathVariable Long id,
            @RequestBody DraftUpdateRequest request) {
        return ResponseEntity.ok(draftService.editDraft(id, request));
    }

    // US-023 + US-024: GET /api/drafts/{id}/export?format=json|markdown
    @GetMapping("/{id}/export")
    public ResponseEntity<?> exportDraft(
            @PathVariable Long id,
            @RequestParam String format) {
        return switch (format.toLowerCase()) {
            case "json" -> ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(draftExportService.exportAsJson(id));
            case "markdown" -> ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(draftExportService.exportAsMarkdown(id));
            default -> ResponseEntity.badRequest()
                    .body("Unsupported format '" + format + "'. Use 'json' or 'markdown'.");
        };
    }
}

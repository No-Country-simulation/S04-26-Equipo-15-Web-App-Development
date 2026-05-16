package com.TalentCircle.bot.review;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.review.dto.DraftResponse;
import com.TalentCircle.bot.review.dto.DraftUpdateRequest;
import com.TalentCircle.bot.review.dto.DraftUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drafts")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
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
}

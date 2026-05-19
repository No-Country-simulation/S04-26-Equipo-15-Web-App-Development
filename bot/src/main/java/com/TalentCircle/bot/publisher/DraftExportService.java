package com.TalentCircle.bot.publisher;

import com.TalentCircle.bot.Entity.Draft;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.Repository.DraftRepository;
import com.TalentCircle.bot.publisher.dto.DraftExportResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DraftExportService {

    private final DraftRepository draftRepository;

    public DraftExportService(DraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    // US-023: exportar borrador aprobado como JSON
    public DraftExportResponse exportAsJson(Long id) {
        Draft draft = findApproved(id);
        return new DraftExportResponse(
                draft.getId(),
                draft.getChannel(),
                draft.getStatus(),
                draft.getContent(),
                draft.getUpdatedAt()
        );
    }

    // US-024: exportar borrador aprobado como Markdown
    public String exportAsMarkdown(Long id) {
        Draft draft = findApproved(id);
        return buildMarkdown(draft);
    }

    private Draft findApproved(Long id) {
        Draft draft = draftRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Draft not found: " + id));

        if (draft.getStatus() != DraftStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Draft " + id + " is not APPROVED (current status: " + draft.getStatus() + ")");
        }
        return draft;
    }

    private String buildMarkdown(Draft draft) {
        return "# Draft Export — " + draft.getChannel() + "\n\n"
                + "**Status:** " + draft.getStatus() + "  \n"
                + "**Approved at:** " + draft.getUpdatedAt() + "  \n\n"
                + "---\n\n"
                + draft.getContent();
    }
}

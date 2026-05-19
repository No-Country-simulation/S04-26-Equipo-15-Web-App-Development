package com.TalentCircle.bot.review;

import com.TalentCircle.bot.Entity.Draft;
import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftHistory;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.Repository.DraftHistoryRepository;
import com.TalentCircle.bot.Repository.DraftRepository;
import com.TalentCircle.bot.review.dto.DraftResponse;
import com.TalentCircle.bot.review.dto.DraftUpdateRequest;
import com.TalentCircle.bot.review.dto.DraftUpdateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DraftService {

    private static final int TWITTER_MAX_CHARS = 280;
    private static final int PREVIEW_MAX_WORDS = 100;

    private final DraftRepository draftRepository;
    private final DraftHistoryRepository draftHistoryRepository;

    public DraftService(DraftRepository draftRepository,
                        DraftHistoryRepository draftHistoryRepository) {
        this.draftRepository = draftRepository;
        this.draftHistoryRepository = draftHistoryRepository;
    }

    // US-019: listar borradores agrupados por canal
    public Map<DraftChannel, List<DraftResponse>> listByStatus(DraftStatus status) {
        return draftRepository.findByStatus(status).stream()
                .collect(Collectors.groupingBy(
                        Draft::getChannel,
                        Collectors.mapping(this::toResponse, Collectors.toList())
                ));
    }

    // US-020: editar borrador guardando historial automático
    @Transactional
    public DraftUpdateResponse editDraft(Long id, DraftUpdateRequest request) {
        Draft draft = draftRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Draft not found: " + id));

        if (draft.getChannel() == DraftChannel.TWITTER
                && request.content().length() > TWITTER_MAX_CHARS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Twitter draft cannot exceed " + TWITTER_MAX_CHARS + " characters");
        }

        DraftHistory history = new DraftHistory();
        history.setDraft(draft);
        history.setPreviousContent(draft.getContent());
        history.setSavedAt(LocalDateTime.now());
        draftHistoryRepository.save(history);

        draft.setContent(request.content());
        draft.setStatus(DraftStatus.EDITED);
        draft.setUpdatedAt(LocalDateTime.now());
        Draft saved = draftRepository.save(draft);

        return toUpdateResponse(saved);
    }

    private DraftResponse toResponse(Draft draft) {
        return new DraftResponse(
                draft.getId(),
                draft.getChannel(),
                draft.getStatus(),
                buildPreview(draft.getContent()),
                draft.getCreatedAt()
        );
    }

    private DraftUpdateResponse toUpdateResponse(Draft draft) {
        return new DraftUpdateResponse(
                draft.getId(),
                draft.getChannel(),
                draft.getStatus(),
                draft.getContent(),
                draft.getUpdatedAt()
        );
    }

    private String buildPreview(String content) {
        if (content == null || content.isBlank()) return "";
        String[] words = content.trim().split("\\s+");
        if (words.length <= PREVIEW_MAX_WORDS) return content;
        return String.join(" ", Arrays.copyOf(words, PREVIEW_MAX_WORDS)) + "...";
    }
}

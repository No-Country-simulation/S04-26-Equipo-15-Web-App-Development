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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DraftServiceTest {

    @Mock
    private DraftRepository draftRepository;

    @Mock
    private DraftHistoryRepository draftHistoryRepository;

    @InjectMocks
    private DraftService draftService;

    private Draft newsletterDraft;
    private Draft twitterDraft;

    @BeforeEach
    void setUp() {
        newsletterDraft = new Draft();
        newsletterDraft.setId(1L);
        newsletterDraft.setChannel(DraftChannel.NEWSLETTER);
        newsletterDraft.setStatus(DraftStatus.PENDING_REVIEW);
        newsletterDraft.setContent("Contenido de newsletter de prueba");
        newsletterDraft.setCreatedAt(LocalDateTime.now());

        twitterDraft = new Draft();
        twitterDraft.setId(2L);
        twitterDraft.setChannel(DraftChannel.TWITTER);
        twitterDraft.setStatus(DraftStatus.PENDING_REVIEW);
        twitterDraft.setContent("Tweet de prueba");
        twitterDraft.setCreatedAt(LocalDateTime.now());
    }

    // US-019 -------------------------------------------------------------------

    @Test
    void listByStatus_returnsGroupedByChannel() {
        when(draftRepository.findByStatus(DraftStatus.PENDING_REVIEW))
                .thenReturn(List.of(newsletterDraft, twitterDraft));

        Map<DraftChannel, List<DraftResponse>> result =
                draftService.listByStatus(DraftStatus.PENDING_REVIEW);

        assertThat(result).containsKeys(DraftChannel.NEWSLETTER, DraftChannel.TWITTER);
        assertThat(result.get(DraftChannel.NEWSLETTER)).hasSize(1);
        assertThat(result.get(DraftChannel.TWITTER)).hasSize(1);
        assertThat(result.get(DraftChannel.NEWSLETTER).get(0).id()).isEqualTo(1L);
    }

    @Test
    void listByStatus_previewTruncatesAt100Words() {
        String longContent = "palabra ".repeat(120).trim();
        newsletterDraft.setContent(longContent);
        when(draftRepository.findByStatus(DraftStatus.PENDING_REVIEW))
                .thenReturn(List.of(newsletterDraft));

        Map<DraftChannel, List<DraftResponse>> result =
                draftService.listByStatus(DraftStatus.PENDING_REVIEW);

        String preview = result.get(DraftChannel.NEWSLETTER).get(0).preview();
        assertThat(preview).endsWith("...");
        assertThat(preview.split("\\s+")).hasSizeLessThanOrEqualTo(101); // 100 words + "..."
    }

    // US-020 -------------------------------------------------------------------

    @Test
    void editDraft_savesHistoryAndSetsEditedStatus() {
        when(draftRepository.findById(1L)).thenReturn(Optional.of(newsletterDraft));
        when(draftRepository.save(any(Draft.class))).thenAnswer(i -> i.getArgument(0));

        DraftUpdateRequest request = new DraftUpdateRequest("Contenido actualizado");
        DraftUpdateResponse response = draftService.editDraft(1L, request);

        ArgumentCaptor<DraftHistory> historyCaptor = ArgumentCaptor.forClass(DraftHistory.class);
        verify(draftHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getPreviousContent())
                .isEqualTo("Contenido de newsletter de prueba");

        assertThat(response.status()).isEqualTo(DraftStatus.EDITED);
        assertThat(response.content()).isEqualTo("Contenido actualizado");
    }

    @Test
    void editDraft_throwsNotFoundWhenDraftMissing() {
        when(draftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> draftService.editDraft(99L, new DraftUpdateRequest("x")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void editDraft_throwsBadRequestWhenTwitterExceedsLimit() {
        when(draftRepository.findById(2L)).thenReturn(Optional.of(twitterDraft));

        String over280 = "a".repeat(281);
        assertThatThrownBy(() -> draftService.editDraft(2L, new DraftUpdateRequest(over280)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");

        verify(draftHistoryRepository, never()).save(any());
    }

    @Test
    void editDraft_acceptsTwitterAtExactLimit() {
        when(draftRepository.findById(2L)).thenReturn(Optional.of(twitterDraft));
        when(draftRepository.save(any(Draft.class))).thenAnswer(i -> i.getArgument(0));

        String exactly280 = "a".repeat(280);
        DraftUpdateResponse response = draftService.editDraft(2L, new DraftUpdateRequest(exactly280));

        assertThat(response.content()).hasSize(280);
        assertThat(response.status()).isEqualTo(DraftStatus.EDITED);
    }
}

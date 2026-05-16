package com.TalentCircle.bot.publisher;

import com.TalentCircle.bot.Entity.Draft;
import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.Repository.DraftRepository;
import com.TalentCircle.bot.publisher.dto.DraftExportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DraftExportServiceTest {

    @Mock
    private DraftRepository draftRepository;

    @InjectMocks
    private DraftExportService draftExportService;

    private Draft approvedDraft;

    @BeforeEach
    void setUp() {
        approvedDraft = new Draft();
        approvedDraft.setId(1L);
        approvedDraft.setChannel(DraftChannel.NEWSLETTER);
        approvedDraft.setStatus(DraftStatus.APPROVED);
        approvedDraft.setContent("Contenido aprobado del newsletter");
        approvedDraft.setCreatedAt(LocalDateTime.now());
        approvedDraft.setUpdatedAt(LocalDateTime.now());
    }

    // US-023 -------------------------------------------------------------------

    @Test
    void exportAsJson_returnsCorrectFields() {
        when(draftRepository.findById(1L)).thenReturn(Optional.of(approvedDraft));

        DraftExportResponse response = draftExportService.exportAsJson(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.channel()).isEqualTo(DraftChannel.NEWSLETTER);
        assertThat(response.status()).isEqualTo(DraftStatus.APPROVED);
        assertThat(response.content()).isEqualTo("Contenido aprobado del newsletter");
        assertThat(response.approvedAt()).isNotNull();
    }

    @Test
    void exportAsJson_throws404_whenNotFound() {
        when(draftRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> draftExportService.exportAsJson(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void exportAsJson_throws400_whenNotApproved() {
        approvedDraft.setStatus(DraftStatus.PENDING_REVIEW);
        when(draftRepository.findById(1L)).thenReturn(Optional.of(approvedDraft));

        assertThatThrownBy(() -> draftExportService.exportAsJson(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    // US-024 -------------------------------------------------------------------

    @Test
    void exportAsMarkdown_containsChannelAndContent() {
        when(draftRepository.findById(1L)).thenReturn(Optional.of(approvedDraft));

        String markdown = draftExportService.exportAsMarkdown(1L);

        assertThat(markdown).contains("# Draft Export — NEWSLETTER");
        assertThat(markdown).contains("**Status:** APPROVED");
        assertThat(markdown).contains("Contenido aprobado del newsletter");
    }

    @Test
    void exportAsMarkdown_throws400_whenNotApproved() {
        approvedDraft.setStatus(DraftStatus.EDITED);
        when(draftRepository.findById(1L)).thenReturn(Optional.of(approvedDraft));

        assertThatThrownBy(() -> draftExportService.exportAsMarkdown(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }
}

package com.TalentCircle.bot.publisher;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.publisher.dto.DraftExportResponse;
import com.TalentCircle.bot.review.DraftController;
import com.TalentCircle.bot.review.DraftService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DraftController.class)
class DraftExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DraftService draftService;

    @MockitoBean
    private DraftExportService draftExportService;

    // US-023 -------------------------------------------------------------------

    @Test
    void GET_export_json_returns200_withJsonBody() throws Exception {
        DraftExportResponse response = new DraftExportResponse(
                1L, DraftChannel.NEWSLETTER, DraftStatus.APPROVED,
                "Contenido aprobado", LocalDateTime.now());

        when(draftExportService.exportAsJson(1L)).thenReturn(response);

        mockMvc.perform(get("/api/drafts/1/export").param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.channel").value("NEWSLETTER"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.content").value("Contenido aprobado"));
    }

    @Test
    void GET_export_json_returns404_whenNotFound() throws Exception {
        when(draftExportService.exportAsJson(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Draft not found: 99"));

        mockMvc.perform(get("/api/drafts/99/export").param("format", "json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void GET_export_json_returns400_whenNotApproved() throws Exception {
        when(draftExportService.exportAsJson(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Draft 1 is not APPROVED"));

        mockMvc.perform(get("/api/drafts/1/export").param("format", "json"))
                .andExpect(status().isBadRequest());
    }

    // US-024 -------------------------------------------------------------------

    @Test
    void GET_export_markdown_returns200_withPlainText() throws Exception {
        String md = "# Draft Export — NEWSLETTER\n\n**Status:** APPROVED\n\nContenido aprobado";
        when(draftExportService.exportAsMarkdown(1L)).thenReturn(md);

        mockMvc.perform(get("/api/drafts/1/export").param("format", "markdown"))
                .andExpect(status().isOk())
                .andExpect(content().string(md));
    }

    @Test
    void GET_export_returns400_forUnsupportedFormat() throws Exception {
        mockMvc.perform(get("/api/drafts/1/export").param("format", "pdf"))
                .andExpect(status().isBadRequest());
    }
}

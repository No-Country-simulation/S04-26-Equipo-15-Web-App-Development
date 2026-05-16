package com.TalentCircle.bot.review;

import com.TalentCircle.bot.Entity.DraftChannel;
import com.TalentCircle.bot.Entity.DraftStatus;
import com.TalentCircle.bot.publisher.DraftExportService;
import com.TalentCircle.bot.review.dto.DraftResponse;
import com.TalentCircle.bot.review.dto.DraftUpdateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DraftControllerTest {

    @Mock
    private DraftService draftService;

    @Mock
    private DraftExportService draftExportService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DraftController controller = new DraftController(draftService, draftExportService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(
                        new StringHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                .build();
    }

    // US-019 -------------------------------------------------------------------

    @Test
    void GET_api_drafts_returns200_groupedByChannel() throws Exception {
        DraftResponse dto = new DraftResponse(
                1L, DraftChannel.NEWSLETTER, DraftStatus.PENDING_REVIEW, "preview texto", null);

        when(draftService.listByStatus(DraftStatus.PENDING_REVIEW))
                .thenReturn(Map.of(DraftChannel.NEWSLETTER, List.of(dto)));

        mockMvc.perform(get("/api/drafts").param("status", "PENDING_REVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.NEWSLETTER").isArray())
                .andExpect(jsonPath("$.NEWSLETTER[0].id").value(1))
                .andExpect(jsonPath("$.NEWSLETTER[0].channel").value("NEWSLETTER"));
    }

    @Test
    void GET_api_drafts_usesDefaultStatusPendingReview() throws Exception {
        when(draftService.listByStatus(DraftStatus.PENDING_REVIEW)).thenReturn(Map.of());

        mockMvc.perform(get("/api/drafts"))
                .andExpect(status().isOk());
    }

    // US-020 -------------------------------------------------------------------

    @Test
    void PUT_api_drafts_id_returns200_withUpdatedDraft() throws Exception {
        DraftUpdateResponse dto = new DraftUpdateResponse(
                1L, DraftChannel.NEWSLETTER, DraftStatus.EDITED, "Contenido editado", null);

        when(draftService.editDraft(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/drafts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Contenido editado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EDITED"))
                .andExpect(jsonPath("$.content").value("Contenido editado"));
    }

    @Test
    void PUT_api_drafts_id_returns404_whenNotFound() throws Exception {
        when(draftService.editDraft(eq(99L), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Draft not found: 99"));

        mockMvc.perform(put("/api/drafts/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"algo\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void PUT_api_drafts_id_returns400_whenTwitterExceedsLimit() throws Exception {
        when(draftService.editDraft(eq(2L), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Twitter draft cannot exceed 280 characters"));

        String over280 = "a".repeat(281);
        mockMvc.perform(put("/api/drafts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"" + over280 + "\"}"))
                .andExpect(status().isBadRequest());
    }
}

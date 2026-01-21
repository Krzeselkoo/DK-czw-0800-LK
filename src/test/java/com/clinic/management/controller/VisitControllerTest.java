package com.clinic.management.controller;

import com.clinic.management.dto.VisitRequest;
import com.clinic.management.dto.VisitSummaryResponse;
import com.clinic.management.service.VisitManagementService;
import com.clinic.management.service.VisitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitService visitService;

    @MockitoBean
    private VisitManagementService visitManagementService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldAddVisit() throws Exception {
        VisitRequest request = new VisitRequest(1L, 2L, LocalDateTime.of(2026, 5, 20, 10, 0));
        when(visitManagementService.addVisit(any(VisitRequest.class))).thenReturn(100L);

        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("100"));
    }

    @Test
    void shouldDeleteVisit() throws Exception {
        doNothing().when(visitService).deleteVisit(100L);

        mockMvc.perform(delete("/api/visits/100"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetVisitById() throws Exception {
        VisitSummaryResponse response = new VisitSummaryResponse(1L, 2L, "A-101", LocalDateTime.of(2026, 5, 20, 10, 0), 15, 5L);
        when(visitService.getVisitById(100L)).thenReturn(response);

        mockMvc.perform(get("/api/visits/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomCode").value("A-101"))
                .andExpect(jsonPath("$.patientId").value(1L));
    }

    @Test
    void shouldGetAllVisits() throws Exception {
        VisitSummaryResponse response = new VisitSummaryResponse(1L, 2L, "A-101", LocalDateTime.now(), 15, 5L);
        when(visitService.getAllVisits()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomCode").value("A-101"));
    }

    @Test
    void shouldGetAllVisitsForPatient() throws Exception {
        VisitSummaryResponse response = new VisitSummaryResponse(1L, 2L, "A-101", LocalDateTime.now(), 15, 5L);
        when(visitService.getAllVisitsForPatient(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/visits/by-patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(1L));
    }

    @Test
    void shouldGetAllVisitsForDoctor() throws Exception {
        VisitSummaryResponse response = new VisitSummaryResponse(1L, 2L, "A-101", LocalDateTime.now(), 15, 5L);
        when(visitService.getAllVisitsForDoctor(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/visits/by-doctor/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorId").value(2L));
    }

    @Test
    void shouldGetAllVisitsForDuty() throws Exception {
        VisitSummaryResponse response = new VisitSummaryResponse(1L, 2L, "A-101", LocalDateTime.now(), 15, 5L);
        when(visitService.getAllVisitsForDuty(5L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/visits/by-duty/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dutyId").value(5L));
    }

    @Test
    void shouldGenerateAvailableHours() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 5, 20, 8, 0);
        // Mapa: Data -> ID dyżuru
        Map<LocalDateTime, Long> availableSlots = Collections.singletonMap(date, 5L);

        when(visitManagementService.generateAvailableHoursForVisit(eq(1L), eq(2L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(availableSlots);

        mockMvc.perform(get("/api/visits/available-hours")
                        .param("patientId", "1")
                        .param("doctorId", "2")
                        .param("from", "2026-05-20T08:00:00")
                        .param("to", "2026-05-20T16:00:00"))
                .andExpect(status().isOk())
                // POPRAWKA: Usunięto ":00" z końca daty w kluczu
                .andExpect(jsonPath("$['2026-05-20T08:00']").value(5));
    }

    @Test
    void shouldReturnBadRequestForInvalidVisitInput() throws Exception {
        VisitRequest invalidRequest = new VisitRequest(null, null, null);

        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
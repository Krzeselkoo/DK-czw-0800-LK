package com.clinic.management.controller;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.model.util.DoctorSpecialization;
import com.clinic.management.service.DoctorService;
import com.clinic.management.service.DutyManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @MockitoBean
    private DutyManagementService dutyManagementService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldCreateDoctor() throws Exception {
        DoctorRequest request = new DoctorRequest("Jan", "Kowalski", "12345678901", DoctorSpecialization.OTOLARYNGOLOGIST, "Warszawa");
        when(doctorService.addDoctor(any(DoctorRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    void shouldGetAllDoctors() throws Exception {
        DoctorSummaryResponse response = new DoctorSummaryResponse(1L, "Jan", "Kowalski", DoctorSpecialization.OTOLARYNGOLOGIST);
        when(doctorService.getAllDoctors()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jan"));
    }

    @Test
    void shouldGetDoctorById() throws Exception {
        DoctorSummaryResponse response = new DoctorSummaryResponse(1L, "Jan", "Kowalski", DoctorSpecialization.OTOLARYNGOLOGIST);
        when(doctorService.getDoctor(1L)).thenReturn(response);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldDeleteDoctor() throws Exception {
        doNothing().when(dutyManagementService).deleteDoctor(1L);

        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        DoctorRequest invalidRequest = new DoctorRequest("", "", "", DoctorSpecialization.OTOLARYNGOLOGIST, "");

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
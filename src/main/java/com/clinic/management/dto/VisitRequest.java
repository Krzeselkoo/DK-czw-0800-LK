package com.clinic.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VisitRequest {
    private Long patientId;
    private Long dutyId;

    @NotNull
    @Schema(type = "string", pattern = "yyyy-MM-dd'T'HH:mm", example = "2025-12-23T10:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
}
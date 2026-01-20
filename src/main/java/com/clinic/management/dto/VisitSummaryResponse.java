package com.clinic.management.dto;

import java.time.LocalDateTime;

public record VisitSummaryResponse(
        Long patientId,
        Long doctorId,
        String roomCode,
        LocalDateTime startTime,
        int durationInMinutes,
        Long dutyId
) {
}

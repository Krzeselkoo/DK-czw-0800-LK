package com.clinic.management.dto;

import java.time.LocalDateTime;

public record DutySummaryResponse(
    Long dutyId,
    Long doctorId,
    Long examRoomId,
    LocalDateTime fromDate,
    LocalDateTime toDate
) {
}

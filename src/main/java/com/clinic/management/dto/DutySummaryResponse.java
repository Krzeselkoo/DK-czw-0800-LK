package com.clinic.management.dto;

import java.time.LocalDate;

public record DutySummaryResponse(
    Long dutyId,
    Long doctorId,
    Long examRoomId,
    LocalDate fromDate,
    LocalDate toDate
) {
}

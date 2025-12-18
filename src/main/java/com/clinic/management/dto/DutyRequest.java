package com.clinic.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DutyRequest {
    private Long doctorId;
    private Long examRoomId;
    private String fromDate; // YYYY-MM-DD
    private String toDate;   // YYYY-MM-DD
}

package com.clinic.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DutyRequest {
    private Long doctorId;
    private Long examRoomId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate; // YYYY-MM-DD

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;   // YYYY-MM-DD
}

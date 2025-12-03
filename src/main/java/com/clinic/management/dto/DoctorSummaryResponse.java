package com.clinic.management.dto;

public record DoctorSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String specialization
) {}

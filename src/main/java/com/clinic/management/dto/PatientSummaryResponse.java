package com.clinic.management.dto;

public record PatientSummaryResponse(
        Long id,
        String firstName,
        String lastName
) {}
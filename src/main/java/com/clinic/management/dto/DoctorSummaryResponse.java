package com.clinic.management.dto;

import com.clinic.management.model.util.DoctorSpecialization;

public record DoctorSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        DoctorSpecialization specialization
) {}

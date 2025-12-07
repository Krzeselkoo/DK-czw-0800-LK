package com.clinic.management.dto;

import com.clinic.management.model.util.DoctorSpecialization;

public record DoctorRequest(
        String firstName,
        String lastName,
        String pesel,
        DoctorSpecialization specialization,
        String address
) {}

package com.clinic.management.dto;

public record DoctorRequest(
        String firstName,
        String lastName,
        String pesel,
        String specialization,
        String address
) {}

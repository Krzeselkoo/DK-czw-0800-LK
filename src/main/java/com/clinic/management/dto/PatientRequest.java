package com.clinic.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientRequest {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotNull(message = "PESEL cannot be null")
    @Pattern(regexp = "\\d{11}", message = "PESEL must be 11 digits")
    private String pesel;

    @NotBlank(message = "Address cannot be blank")
    private String address;
}
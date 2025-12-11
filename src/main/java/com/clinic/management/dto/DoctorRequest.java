package com.clinic.management.dto;

import com.clinic.management.model.util.DoctorSpecialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorRequest {

    @NotBlank(message = "First name cannot be blank")
    String firstName;

    @NotBlank(message = "Last name cannot be blank")
    String lastName;

    @NotNull(message = "PESEL cannot be null")
    @Pattern(regexp = "\\d{11}", message = "PESEL must be 11 digits")
    String pesel;

    @NotNull(message = "Specialization cannot be null")
    DoctorSpecialization specialization;

    @NotBlank(message = "Address cannot be blank")
     String address;
}
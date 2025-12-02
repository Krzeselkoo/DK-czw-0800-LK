package com.clinic.management.controller;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addDoctor(@RequestBody DoctorRequest doctorRequest) {
        doctorService.addDoctor(doctorRequest);
    }
    
    @GetMapping
    public List<DoctorSummaryResponse> getDoctors() {
        return doctorService.getAllDoctors();
    }
}

package com.clinic.management.service;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.model.Doctor;
import com.clinic.management.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;


    public void addDoctor(DoctorRequest request) {
        Doctor doctor = Doctor.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .pesel(request.pesel())
                .specialization(request.specialization())
                .address(request.address())
                .build();

        doctorRepository.save(doctor);
    }

    public List<DoctorSummaryResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> new DoctorSummaryResponse(
                        doctor.getId(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getSpecialization()
                ))
                .collect(Collectors.toList());
    }
}

package com.clinic.management.service;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.model.entity.Doctor;
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
        Doctor doctor = new Doctor(
                request.firstName(),
                request.lastName(),
                request.pesel(),
                request.specialization(),
                request.address());


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

    public Doctor getDoctor(long doctorID) {
        return doctorRepository.findById(doctorID).orElse(null);
    }

    public boolean deleteDoctor(long doctorID) {
        try {
            if (!doctorRepository.existsById(doctorID)) {
                return false;
            }
            doctorRepository.deleteById(doctorID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

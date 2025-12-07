package com.clinic.management.service;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.exception.DoctorNotFoundException;
import com.clinic.management.exception.DuplicatePeselException;
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

    /**
     * Adds a new doctor to the repository.
     *
     * @param request the request object containing doctor details
     * @throws DuplicatePeselException if a doctor with the same PESEL already exists
     */
    public void addDoctor(DoctorRequest request) {
        if (doctorRepository.existsByPesel(request.getPesel())) {
            throw new DuplicatePeselException("Doctor with PESEL" + request.getPesel());
        }
        Doctor doctor = new Doctor(
                request.getFirstName(),
                request.getLastName(),
                request.getPesel(),
                request.getSpecialization(),
                request.getAddress()
        );


        doctorRepository.save(doctor);
    }

    /**
     * Retrieves all doctors from the repository.
     *
     * @return a list of doctor summary responses
     */
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

    /**
     * Retrieves a specific doctor by their ID.
     *
     * @param doctorID the ID of the doctor to retrieve
     * @return the doctor entity
     * @throws DoctorNotFoundException if no doctor is found with the given ID
     */
    public Doctor getDoctor(long doctorID) {
        return doctorRepository.findById(doctorID).orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorID));
    }

    /**
     * Deletes a specific doctor by their ID.
     *
     * @param doctorID the ID of the doctor to delete
     * @return true if the doctor was successfully deleted, false otherwise
     */
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

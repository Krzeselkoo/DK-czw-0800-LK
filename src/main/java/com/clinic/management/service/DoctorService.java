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
     * @return new doctor's ID
     */
    public long addDoctor(DoctorRequest request) {
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

        doctor = doctorRepository.save(doctor);
        return doctor.getId();
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
     * @return the doctor summary response
     * @throws DoctorNotFoundException if no doctor is found with the given ID
     */
    public DoctorSummaryResponse getDoctor(long doctorID) {
        Doctor doctor = doctorRepository.findById(doctorID).orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorID));
        return new DoctorSummaryResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization()
        );
    }

    /**
     * Deletes a specific doctor by their ID.
     *
     * @param doctorID the ID of the doctor to delete
     * @throws DoctorNotFoundException when there is no doctor with supplied ID
     */
    public void deleteDoctor(long doctorID) {
        if (!doctorRepository.existsById(doctorID)) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + doctorID);
        }
        doctorRepository.deleteById(doctorID);
    }
}

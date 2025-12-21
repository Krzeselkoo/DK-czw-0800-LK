package com.clinic.management.service;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.dto.DutySummaryResponse;
import com.clinic.management.exception.DoctorNotFoundException;
import com.clinic.management.exception.DuplicatePeselException;
import com.clinic.management.exception.DutyConflictException;
import com.clinic.management.model.entity.Doctor;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.DutyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DutyService dutyService;

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

        Doctor doctor = Doctor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .pesel(request.getPesel())
                .specialization(request.getSpecialization())
                .address(request.getAddress())
                .build();


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
        Doctor doctor = getDoctorEntity(doctorID);
        return new DoctorSummaryResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization()
        );
    }

    /**
     * Retrieves a specific doctor entity by their ID. Used only for other services
     *
     * @param doctorID the ID of the doctor to retrieve
     * @return the Doctor object
     * @throws DoctorNotFoundException if no doctor is found with the given ID
     */
    public Doctor getDoctorEntity(long doctorID) {
        return doctorRepository.findById(doctorID).orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorID));
    }

    /**
     * Deletes a specific doctor by their ID.
     *
     * @param doctorID the ID of the doctor to delete
     * @throws DoctorNotFoundException when there is no doctor with supplied ID
     * @throws DutyConflictException when the doctor is assigned to duties and cannot be deleted
     */
    public void deleteDoctor(long doctorID) {
        if (!doctorRepository.existsById(doctorID)) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + doctorID);
        }

        if (dutyService.existsByDoctorId(doctorID)) {
            throw new DutyConflictException("Cannot delete doctor. Doctor is assigned to duties.");
        }
        doctorRepository.deleteById(doctorID);
    }

    /**
     * Retrieves all doctors available between the given dates.
     *
     * @param from start of the searched time window
     * @param to end of the searched time window
     * @return a list of doctor summary response objects
     */
    public List<DoctorSummaryResponse>  getAllAvailableDoctors(LocalDate from, LocalDate to){
        return doctorRepository.findAvailableDoctors(from, to).stream()
                .map(doctor -> new DoctorSummaryResponse(doctor.getId(), doctor.getFirstName(), doctor.getLastName(), doctor.getSpecialization()))
                .toList();
    }

    public List<DutySummaryResponse> getAllDutiesForDoctor(long id){
        return dutyService.getAllDutiesForDoctor(id);
    }
}
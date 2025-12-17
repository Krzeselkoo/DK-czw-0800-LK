package com.clinic.management.service;

import com.clinic.management.dto.PatientRequest;
import com.clinic.management.dto.PatientSummaryResponse;
import com.clinic.management.exception.DuplicatePeselException;
import com.clinic.management.exception.PatientNotFoundException;
import com.clinic.management.model.entity.Patient;
import com.clinic.management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Adds a new patient to the repository.
     *
     * @param request the request object containing patient details
     * @throws DuplicatePeselException if a patient with the same PESEL already exists
     * @return new patient's ID
     */
    public long addPatient(PatientRequest request) {
        if (patientRepository.existsByPesel(request.getPesel())) {
            throw new DuplicatePeselException("Patient with PESEL " + request.getPesel() + " already exists.");
        }

        Patient patient = new Patient(
                request.getFirstName(),
                request.getLastName(),
                request.getPesel(),
                request.getAddress()
        );

        patient = patientRepository.save(patient);
        return patient.getId();
    }

    /**
     * Retrieves all patients from the repository.
     *
     * @return a list of patient summary responses
     */
    public List<PatientSummaryResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patient -> new PatientSummaryResponse(
                        patient.getId(),
                        patient.getFirstName(),
                        patient.getLastName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific patient by their ID.
     *
     * @param id the ID of the patient to retrieve
     * @return the patient summary response
     * @throws PatientNotFoundException if no patient is found with the given ID
     */
    public PatientSummaryResponse getPatient(long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        return new PatientSummaryResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName()
        );
    }

    /**
     * Deletes a specific patient by their ID.
     *
     * @param id the ID of the patient to delete
     * @throws PatientNotFoundException when there is no patient with supplied ID
     */
    public void deletePatient(long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with ID: " + id);
        }
        patientRepository.deleteById(id);
    }
}
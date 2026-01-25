package com.clinic.management.service;

import com.clinic.management.config.AppConfig;
import com.clinic.management.dto.VisitRequest;
import com.clinic.management.exception.DutyNotFoundException;
import com.clinic.management.exception.PatientNotFoundException;
import com.clinic.management.exception.VisitConflictException;
import com.clinic.management.model.entity.Duty;
import com.clinic.management.model.entity.Patient;
import com.clinic.management.model.entity.Visit;
import com.clinic.management.repository.DutyRepository;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VisitManagementService {
    private final PatientRepository patientRepository;
    private final DutyRepository dutyRepository;
    private final VisitRepository visitRepository;
    private final AppConfig appConfig;

    /**
     * Creates a new visit in the system.
     *
     * @param request the `VisitRequest` object containing the details of the visit to be created
     * @return the ID of the newly created visit
     */
    @Transactional
    public Long addVisit(VisitRequest request){
        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID:" + request.getPatientId())
        );

        Duty duty = dutyRepository.findById(request.getDutyId()).orElseThrow(
                () -> new DutyNotFoundException("Duty not found with ID:" + request.getDutyId())
        );

        visitRepository.findVisitByDutyIdAndStartDateWithLock(duty.getId(), request.getStartDate())
                .ifPresent(visit -> {
                    throw new VisitConflictException("The selected time slot is no longer available.");
                });

        Visit visit = Visit.builder()
                .patient(patient)
                .duty(duty)
                .startDate(request.getStartDate())
                .build();

        Visit saveVisit = visitRepository.save(visit);

        return saveVisit.getId();
    }

    /**
     * Generates possible time windows (start of each time window) for the chosen doctor by the given patient in the given date range.
     *
     * @param patientId the ID of the patient who makes an appointment
     * @param doctorId the ID of the doctor whom with the appointment is made
     * @param from the date from which the dates will be generated
     * @param to the date to which the dates will generate
     * @return map of dates and corresponding duty IDs.
     */
    public Map<LocalDateTime, Long> generateAvailableHoursForVisit(Long patientId, Long doctorId, LocalDateTime from, LocalDateTime to) {
        List<Duty> doctorDuties = dutyRepository.findDutiesByDoctorIdAndDateRange(doctorId, from, to);
        List<Visit> patientVisits = visitRepository.findVisitsByPatientIdAndDateRange(patientId, from, to);
        List<Visit> doctorVisits = visitRepository.findVisitsByDoctorIdAndDateRange(doctorId, from, to);
        Map<LocalDateTime, Long> availableSlots = new LinkedHashMap<>();

        LocalDateTime now = LocalDateTime.now();

        for (Duty duty : doctorDuties) {
            LocalDateTime dutyStart = duty.getFromDate().isBefore(from) ? from : duty.getFromDate();
            LocalDateTime dutyEnd = duty.getToDate().isAfter(to) ? to : duty.getToDate();

            if (dutyStart.isAfter(dutyEnd)) {
                continue;
            }

            while (dutyStart.isBefore(dutyEnd)) {
                LocalDateTime slotEnd = dutyStart.plusMinutes(appConfig.getVisitDuration());

                if (slotEnd.isAfter(dutyEnd) || dutyStart.isBefore(now)) {
                    dutyStart = slotEnd;
                    continue;
                }

                if (isSlotAvailable(patientVisits, doctorVisits, dutyStart, slotEnd)) {
                    availableSlots.put(dutyStart, duty.getId());
                }
                dutyStart = slotEnd;
            }
        }
        return availableSlots;
    }

    private boolean isSlotAvailable(List<Visit> patientVisits, List<Visit> doctorVisits, LocalDateTime dutyStart, LocalDateTime slotEnd) {
        boolean isPatientSlotAvailable = patientVisits.stream().noneMatch(visit -> {
            LocalDateTime visitEnd = visit.getStartDate().plusMinutes(appConfig.getVisitDuration());
            return visit.getStartDate().isBefore(slotEnd) && visitEnd.isAfter(dutyStart);
        });

        boolean isDoctorSlotAvailable = doctorVisits.stream().noneMatch(visit -> {
            LocalDateTime visitEnd = visit.getStartDate().plusMinutes(appConfig.getVisitDuration());
            return visit.getStartDate().isBefore(slotEnd) && visitEnd.isAfter(dutyStart);
        });

        return isPatientSlotAvailable && isDoctorSlotAvailable;
    }
    
    /**
     * Deletes a specific duty by their ID.
     *
     * @param dutyID the ID of the duty to delete
     * @throws DutyNotFoundException when there is no duty with supplied ID
     */
    public void deleteDuty(Long dutyID) {
        if(!dutyRepository.existsById(dutyID)){
            throw new DutyNotFoundException("Duty not found with ID:" + dutyID);
        }
        if(visitRepository.existsByDutyId(dutyID)){
            throw new VisitConflictException("Cannot delete duty. Duty has assigned visits");
        }

        dutyRepository.deleteById(dutyID);
    }

    /**
     * Deletes a specific patient by their ID.
     *
     * @param patientId the ID of the patient to delete
     * @throws PatientNotFoundException when there is no patient with supplied ID
     */
    @Transactional
    public void deletePatient(long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        }

        if(visitRepository.existsByPatientId(patientId)){
            throw new VisitConflictException("Cannot delete patient. Patient is assigned to visits");
        }

        patientRepository.deleteById(patientId);
    }
}

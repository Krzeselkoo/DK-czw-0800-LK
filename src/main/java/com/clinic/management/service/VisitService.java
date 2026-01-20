package com.clinic.management.service;

import com.clinic.management.dto.VisitSummaryResponse;
import com.clinic.management.exception.VisitNotFoundException;
import com.clinic.management.model.entity.Visit;
import com.clinic.management.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final int VISIT_DURATION = 15;
    private final VisitRepository visitRepository;

    /**
     * Retrieves all visits from the repository.
     *
     * @return a list of `VisitSummaryResponse` objects representing all visits
     */
    public List<VisitSummaryResponse> getAllVisits(){
        return visitRepository.findAll()
                .stream()
                .map(visit -> new VisitSummaryResponse(
                        visit.getPatient().getId(),
                        visit.getDuty().getDoctor().getId(),
                        visit.getDuty().getExamRoom().getRoomCode(),
                        visit.getStartDate(),
                        VISIT_DURATION,
                        visit.getDuty().getId()
                )).collect(Collectors.toList());
    }

    /**
     * Retrieves a specific visit by its ID.
     *
     * @param visitId the ID of the visit to retrieve
     * @return a `VisitSummaryResponse` object representing the visit
     * @throws VisitNotFoundException if the visit with the given ID is not found
     */
    public VisitSummaryResponse getVisitById(Long visitId){
        Visit visit = getVisitEntity(visitId);
        return new VisitSummaryResponse(
                visit.getPatient().getId(),
                visit.getDuty().getDoctor().getId(),
                visit.getDuty().getExamRoom().getRoomCode(),
                visit.getStartDate(),
                VISIT_DURATION,
                visit.getDuty().getId()
        );
    }

    /**
     * Retrieves all visits for a specific doctor by their ID.
     *
     * @param doctorId the ID of the doctor whose visits are to be retrieved
     * @return a list of `VisitSummaryResponse` objects representing the doctor's visits
     */
    public List<VisitSummaryResponse> getAllVisitsForDoctor(Long doctorId){
        return getAllVisitsForDefault(doctorId, visitRepository::findAllByDoctorId);
    }

    /**
     * Retrieves all visits for a specific patient by their ID.
     *
     * @param patientId the ID of the patient whose visits are to be retrieved
     * @return a list of `VisitSummaryResponse` objects representing the patient's visits
     */
    public List<VisitSummaryResponse> getAllVisitsForPatient(Long patientId){
        return getAllVisitsForDefault(patientId, visitRepository::findAllByPatientId);
    }

    /**
     * Retrieves all visits for a specific duty by its ID.
     *
     * @param dutyId the ID of the duty whose visits are to be retrieved
     * @return a list of `VisitSummaryResponse` objects representing the duty's visits
     */
    public List<VisitSummaryResponse> getAllVisitsForDuty(Long dutyId){
        return getAllVisitsForDefault(dutyId, visitRepository::findAllByDutyId);
    }


    /**
     * Retrieves all visits for a specific entity (doctor, patient, or duty) by its ID.
     *
     * This is a helper method that uses a function to fetch visits based on the provided ID
     * and maps them to `VisitSummaryResponse` objects.
     *
     * @param id the ID of the entity (doctor, patient, or duty) whose visits are to be retrieved
     * @param function a function that fetches visits based on the provided ID
     * @return a list of `VisitSummaryResponse` objects representing the visits
     */
    private List<VisitSummaryResponse> getAllVisitsForDefault(Long id,
                                                              Function<Long, List<Visit>> function){
        return function.apply(id)
                .stream()
                .map(visit -> new VisitSummaryResponse(
                        visit.getPatient().getId(),
                        visit.getDuty().getDoctor().getId(),
                        visit.getDuty().getExamRoom().getRoomCode(),
                        visit.getStartDate(),
                        VISIT_DURATION,
                        visit.getDuty().getId()
                )).collect(Collectors.toList());
    }

    /**
     * Retrieves a visit entity by its ID.
     *
     * @param visitId the ID of the visit to retrieve
     * @return the `Visit` entity
     * @throws VisitNotFoundException if the visit with the given ID is not found
     */
    private Visit getVisitEntity(Long visitId){
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found with ID:" + visitId));
    }

    /**
     * Deletes a specific visit by its ID.
     *
     * @param id the ID of the visit to delete
     * @throws VisitNotFoundException if the visit with the given ID is not found
     */
    @Transactional
    public void deleteVisit(Long id){
        if(!visitRepository.existsById(id)){
            throw new VisitNotFoundException("Visit not found with ID: " + id);
        }

        visitRepository.deleteById(id);
    }

}

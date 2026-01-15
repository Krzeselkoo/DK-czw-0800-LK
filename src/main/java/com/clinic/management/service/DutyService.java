package com.clinic.management.service;

import com.clinic.management.dto.DutyRequest;
import com.clinic.management.dto.DutySummaryResponse;
import com.clinic.management.dto.ExamRoomSummaryResponse;
import com.clinic.management.exception.*;
import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.entity.Duty;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.repository.DutyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DutyService {
    public final DutyRepository dutyRepository;

    /**
     * Retrieves all duties.
     *
     * @return the list of duty summary responses
     */
    public List<DutySummaryResponse> getAllDuties(){
        return dutyRepository.findAll().stream()
                .map(duty -> new DutySummaryResponse(
                        duty.getId(),
                        duty.getDoctor().getId(),
                        duty.getExamRoom().getId(),
                        duty.getFromDate(),
                        duty.getToDate()
                )).toList();
    }
    /**
     * Retrieves a specific duty by their ID.
     *
     * @param dutyID the ID of the duty to retrieve
     * @return the duty summary response
     * @throws DutyNotFoundException if no duty is found with the given ID
     */
    public DutySummaryResponse getDuty(Long dutyID){
        Duty duty = dutyRepository.findById(dutyID)
                .orElseThrow(() -> new DutyNotFoundException("Duty not found with ID:" + dutyID));
        return new DutySummaryResponse(
                duty.getId(),
                duty.getDoctor().getId(),
                duty.getExamRoom().getId(),
                duty.getFromDate(),
                duty.getToDate()
        );
    };
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
        dutyRepository.deleteById(dutyID);
    }

    /**
     * Returns a list of duties for doctor with given ID.
     *
     * @param id the ID of the doctor whose duties will be retrieved from the database
     */
    public List<DutySummaryResponse> getAllDutiesForDoctor(long id) {
        return dutyRepository.getDutiesByDoctorId(id).stream()
                .map(duty -> new DutySummaryResponse(
                        duty.getId(),
                        duty.getDoctor().getId(),
                        duty.getExamRoom().getId(),
                        duty.getFromDate(),
                        duty.getToDate()
                )).toList();
    }

    /**
     * Returns a list of duties for exam room with given ID.
     *
     * @param id the ID of the room which duties will be retrieved from the database
     */
    public List<DutySummaryResponse> getAllDutiesForExamRoom(long id) {
        return dutyRepository.getDutiesByExamRoomId(id).stream()
                .map(duty -> new DutySummaryResponse(
                        duty.getId(),
                        duty.getDoctor().getId(),
                        duty.getExamRoom().getId(),
                        duty.getFromDate(),
                        duty.getToDate()
                )).toList();
    }
}

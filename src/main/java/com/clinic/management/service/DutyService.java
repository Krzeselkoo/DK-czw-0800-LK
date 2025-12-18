package com.clinic.management.service;

import com.clinic.management.dto.DutyRequest;
import com.clinic.management.dto.DutySummaryResponse;
import com.clinic.management.exception.*;
import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.entity.Duty;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.repository.DutyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DutyService {
    public final DutyRepository dutyRepository;
    public final DoctorService doctorService;
    public final ExamRoomService examRoomService;
    /**
     * Adds a new duty to the repository.
     *
     * @param request the request object containing duty details
     * @throws DoctorNotFoundException if a doctor with given id was not found
     * @throws ExamRoomNotFoundException if an exam room with given id was not found
     * @throws DoctorBusyAtDateException if the doctor is already assigned to another duty during the specified period
     * @throws RoomOccupiedAtDateException if the exam room is already occupied during the specified period
     * @return new duty's ID
     */
    public long addDuty(DutyRequest request) {
        LocalDate from = request.getFromDate();
        LocalDate to = request.getToDate();

        Doctor doctor = doctorService.getDoctorEntity(request.getDoctorId());
        if(dutyRepository.existingDoctorIsBusyAtThisTime(doctor, from, to)) {
            throw new DoctorBusyAtDateException("Doctor already has a duty during this period.");
        }

        ExamRoom examRoom = examRoomService.getExamRoomEntity(request.getExamRoomId());
        if(dutyRepository.existingRoomIsOccupiedAtThisTime(examRoom, from, to)) {
            throw new RoomOccupiedAtDateException("Exam room is already occupied during this period.");
        }

        Duty duty = Duty.builder()
                .doctor(doctor)
                .examRoom(examRoom)
                .fromDate(from)
                .toDate(to)
                .build();
        
        return duty.getId();
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
}

package com.clinic.management.service;

import com.clinic.management.dto.DutyRequest;
import com.clinic.management.exception.*;
import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.entity.Duty;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.DutyRepository;
import com.clinic.management.repository.ExamRoomRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DutyManagementService {
    private final DoctorService doctorService;
    private final ExamRoomService examRoomService;
    private final DutyRepository dutyRepository;
    private final ExamRoomRepository examRoomRepository;
    private final DoctorRepository doctorRepository;
    /**
     * Adds a new duty to the repository.
     *
     * @param request the request object containing duty details
     * @throws InvalidTimeWindowException if the given time window is not valid (has to be between 1 and 8 hours)
     * @throws DoctorNotFoundException if a doctor with given id was not found
     * @throws ExamRoomNotFoundException if an exam room with given id was not found
     * @throws DoctorBusyAtDateException if the doctor is already assigned to another duty during the specified period
     * @throws RoomOccupiedAtDateException if the exam room is already occupied during the specified period
     * @return new duty's ID
     */
    @Transactional
    public long addDuty(DutyRequest request) {
        LocalDateTime from = request.getFromDate();
        LocalDateTime to = request.getToDate();

        checkCorrectTimeWindow(from, to);

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

        Duty saveDuty = dutyRepository.save(duty);

        return saveDuty.getId();
    }

    /**
     * Deletes a specific exam room by their ID.
     *
     * @param roomID the ID of the room to delete
     * @throws ExamRoomNotFoundException when there is no room with supplied ID
     * @throws DutyConflictException when the room is assigned to duties and cannot be deleted
     */
    @Transactional
    public void deleteExamRoom(long roomID) {
        if (!examRoomRepository.existsById(roomID)) {
            throw new ExamRoomNotFoundException("Exam room not found with ID: " + roomID);
        }

        if (dutyRepository.existsByExamRoomId(roomID)) {
            throw new DutyConflictException("Cannot delete exam room. Room is assigned to duties.");
        }
        examRoomRepository.deleteById(roomID);
    }

    /**
     * Deletes a specific doctor by their ID.
     *
     * @param doctorID the ID of the doctor to delete
     * @throws DoctorNotFoundException when there is no doctor with supplied ID
     * @throws DutyConflictException when the doctor is assigned to duties and cannot be deleted
     */
    @Transactional
    public void deleteDoctor(long doctorID) {
        if (!doctorRepository.existsById(doctorID)) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + doctorID);
        }

        if (dutyRepository.existsByDoctorId(doctorID)) {
            throw new DutyConflictException("Cannot delete doctor. Doctor is assigned to duties.");
        }
        doctorRepository.deleteById(doctorID);
    }

    private void checkCorrectTimeWindow(LocalDateTime from, LocalDateTime to) throws InvalidTimeWindowException{
        if(from.isEqual(to) || from.isAfter(to)){
            throw new InvalidTimeWindowException("The FROM date has to be before TO date.");
        }

        long durationInMinutes = Duration.between(from, to).toMinutes();

        if(durationInMinutes < 60 || durationInMinutes > 480){
            throw new InvalidTimeWindowException("The time window has to be between 1 hour and 8 hours (inclusive) in length");
        }

        int fromHour = from.getHour();
        int toHour = to.getHour();
        int toMinutes = to.getMinute();

        if(fromHour < 8 || toHour > 16 || (toHour == 16 && toMinutes != 0)){
            throw new InvalidTimeWindowException("The clinic works from 8 AM to 4PM (8-16).");
        }
    }
}

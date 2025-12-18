package com.clinic.management.service;

import com.clinic.management.dto.DutyRequest;
import com.clinic.management.exception.DoctorBusyAtDateException;
import com.clinic.management.exception.RoomOccupiedAtDateException;
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

    public long addDuty(DutyRequest request) {
        LocalDate from = LocalDate.parse(request.getFromDate());
        LocalDate to = LocalDate.parse(request.getToDate());

        Doctor doctor = doctorService.getDoctorEntity(request.getDoctorId());
        if(dutyRepository.existingDoctorIsBusyAtThisTime(doctor, from, to)) {
            throw new DoctorBusyAtDateException("Doctor already has a duty during this period.");
        }

        ExamRoom examRoom = examRoomService.getExamRoomEntity(request.getExamRoomId());
        if(dutyRepository.existingRoomIsOccupiedAtThisTime(examRoom, from, to)) {
            throw new RoomOccupiedAtDateException("Exam room is already occupied during this period.");
        }

        Duty duty = new Duty(
                doctor,
                examRoom,
                from,
                to
        );
        
        return duty.getId();
    }
}

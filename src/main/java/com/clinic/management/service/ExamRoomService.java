package com.clinic.management.service;


import com.clinic.management.dto.ExamRoomRequest;
import com.clinic.management.dto.ExamRoomSummaryResponse;
import com.clinic.management.exception.DuplicateRoomCodeException;
import com.clinic.management.exception.ExamRoomNotFoundException;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.repository.ExamRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamRoomService {
    public final ExamRoomRepository examRoomRepository;

    /**
     * Retrieves an exam room by their ID.
     *
     * @param roomID the ID of the room to retrieve
     * @return the exam room summary response
     * @throws ExamRoomNotFoundException if no room is found with the given ID
     */
    public ExamRoomSummaryResponse getExamRoom(Long roomID){
        ExamRoom examRoom = getExamRoomEntity(roomID);
        return new ExamRoomSummaryResponse(
                examRoom.getId(),
                examRoom.getRoomCode(),
                examRoom.getRoomType()
        );
    };

    public ExamRoom getExamRoomEntity(Long roomID){
        return examRoomRepository.findById(roomID)
                .orElseThrow(() -> new ExamRoomNotFoundException("Exam room not found with ID:" + roomID));
    }

    /**
     * Retrieves all exam rooms from the repository.
     *
     * @return a list of exam rooms summary responses
     */
    public List<ExamRoomSummaryResponse> getAllExamRooms() {
        return examRoomRepository.findAll()
                .stream()
                .map(room -> new ExamRoomSummaryResponse(
                        room.getId(),
                        room.getRoomCode(),
                        room.getRoomType()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new exam room to the repository.
     *
     * @param request the request object containing room's details
     * @throws DuplicateRoomCodeException if a room with the same code already exists
     * @return new room's ID
     */
    @Transactional
    public Long addExamRoom(ExamRoomRequest request){
        if(examRoomRepository.existsByRoomCode(request.getRoomCode())){
            throw new DuplicateRoomCodeException("Room with code " + request.getRoomCode() + " is already in the database.");
        }
        ExamRoom examRoom = ExamRoom.builder()
                .roomCode(request.getRoomCode())
                .roomType(request.getRoomType())
                .build();

        examRoomRepository.save(examRoom);
        return examRoom.getId();
    }

    /**
     * Retrieves all rooms available between the given dates.
     *
     * @param from start of the searched time window
     * @param to end of the searched time window
     * @return a list of exam room summary response objects
     */
    public List<ExamRoomSummaryResponse>  getAllAvailableRooms(LocalDateTime from, LocalDateTime to){
        return examRoomRepository.findAvailableRooms(from, to).stream()
                .map(er -> new ExamRoomSummaryResponse(er.getId(), er.getRoomCode(), er.getRoomType()))
                .toList();
    }

}
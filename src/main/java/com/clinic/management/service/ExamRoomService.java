package com.clinic.management.service;

import com.clinic.management.dto.ExamRoomRequest;
import com.clinic.management.dto.ExamRoomSummaryResponse;
import com.clinic.management.exception.DuplicateRoomCodeException;
import com.clinic.management.exception.DutyConflictException;
import com.clinic.management.exception.ExamRoomNotFoundException;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.repository.DutyRepository;
import com.clinic.management.repository.ExamRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamRoomService {
    public final ExamRoomRepository examRoomRepository;
    private final DutyRepository dutyRepository;

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
     * Deletes a specific exam room by their ID.
     *
     * @param roomID the ID of the room to delete
     * @throws ExamRoomNotFoundException when there is no room with supplied ID
     * @throws DutyConflictException when the room is assigned to duties and cannot be deleted
     */
    public void deleteExamRoom(long roomID) {
        if (!examRoomRepository.existsById(roomID)) {
            throw new ExamRoomNotFoundException("Exam room not found with ID: " + roomID);
        }
        // Check if room has assigned duties
        if (dutyRepository.existsByExamRoomId(roomID)) {
            throw new DutyConflictException("Cannot delete exam room. Room is assigned to duties.");
        }
        examRoomRepository.deleteById(roomID);
    }
}
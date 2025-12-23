package com.clinic.management.service;

import com.clinic.management.dto.ExamRoomRequest;
import com.clinic.management.dto.ExamRoomSummaryResponse;
import com.clinic.management.exception.DuplicateRoomCodeException;
import com.clinic.management.exception.DutyConflictException;
import com.clinic.management.exception.ExamRoomNotFoundException;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.model.util.RoomType;
import com.clinic.management.repository.DutyRepository;
import com.clinic.management.repository.ExamRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamRoomServiceTest {

    @Mock
    private ExamRoomRepository examRoomRepository;

    @Mock
    private DutyRepository dutyRepository;

    @InjectMocks
    private ExamRoomService examRoomService;

    @Test
    void shouldAddExamRoomSuccessfully() {
        ExamRoomRequest request = new ExamRoomRequest("A-101", RoomType.GENERAL);

        when(examRoomRepository.existsByRoomCode("A-101")).thenReturn(false);
        when(examRoomRepository.save(any(ExamRoom.class))).thenAnswer(inv -> {
            ExamRoom room = inv.getArgument(0);
            room.setId(1L);
            return room;
        });

        long id = examRoomService.addExamRoom(request);

        assertEquals(1L, id);
        verify(examRoomRepository).save(any(ExamRoom.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateRoomCode() {
        ExamRoomRequest request = new ExamRoomRequest("A-101", RoomType.GENERAL);
        when(examRoomRepository.existsByRoomCode("A-101")).thenReturn(true);

        assertThrows(DuplicateRoomCodeException.class, () -> examRoomService.addExamRoom(request));
        verify(examRoomRepository, never()).save(any());
    }

    @Test
    void shouldGetAllExamRooms() {
        ExamRoom r1 = ExamRoom.builder()
                .roomCode("A-101")
                .roomType(RoomType.GENERAL)
                .build();

        ExamRoom r2 = ExamRoom.builder()
                .roomCode("B-202")
                .roomType(RoomType.SURGERY)
                .build();
        when(examRoomRepository.findAll()).thenReturn(List.of(r1, r2));

        List<ExamRoomSummaryResponse> result = examRoomService.getAllExamRooms();

        assertEquals(2, result.size());
        assertEquals("A-101", result.get(0).roomCode());
    }
}
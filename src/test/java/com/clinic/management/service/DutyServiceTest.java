package com.clinic.management.service;

import com.clinic.management.dto.DutySummaryResponse;
import com.clinic.management.exception.DutyNotFoundException;
import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.entity.Duty;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.repository.DutyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DutyServiceTest {

    @Mock
    private DutyRepository dutyRepository;

    @InjectMocks
    private DutyService dutyService;

    @Test
    void shouldGetAllDuties() {
        // Given
        Long dutyId = 10L;
        Long doctorId = 1L;
        Long roomId = 2L;
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusHours(4);

        // Tworzymy mocki encji, aby uniknąć problemów z Builderem i @NonNull
        Doctor doctor = mock(Doctor.class);
        when(doctor.getId()).thenReturn(doctorId);

        ExamRoom room = mock(ExamRoom.class);
        when(room.getId()).thenReturn(roomId);

        Duty duty = mock(Duty.class);
        when(duty.getId()).thenReturn(dutyId);
        when(duty.getDoctor()).thenReturn(doctor);
        when(duty.getExamRoom()).thenReturn(room);
        when(duty.getFromDate()).thenReturn(from);
        when(duty.getToDate()).thenReturn(to);

        when(dutyRepository.findAll()).thenReturn(List.of(duty));

        // When
        List<DutySummaryResponse> result = dutyService.getAllDuties();

        // Then
        assertEquals(1, result.size());
        assertEquals(dutyId, result.get(0).dutyId());
        assertEquals(doctorId, result.get(0).doctorId());
        assertEquals(roomId, result.get(0).examRoomId());
    }

    @Test
    void shouldGetDutyById() {
        // Given
        Long dutyId = 10L;

        Doctor doctor = mock(Doctor.class);
        when(doctor.getId()).thenReturn(1L);

        ExamRoom room = mock(ExamRoom.class);
        when(room.getId()).thenReturn(2L);

        Duty duty = mock(Duty.class);
        when(duty.getId()).thenReturn(dutyId);
        when(duty.getDoctor()).thenReturn(doctor);
        when(duty.getExamRoom()).thenReturn(room);
        when(duty.getFromDate()).thenReturn(LocalDateTime.now());
        when(duty.getToDate()).thenReturn(LocalDateTime.now().plusHours(4));

        when(dutyRepository.findById(dutyId)).thenReturn(Optional.of(duty));

        // When
        DutySummaryResponse result = dutyService.getDuty(dutyId);

        // Then
        assertNotNull(result);
        assertEquals(dutyId, result.dutyId());
    }

    @Test
    void shouldThrowExceptionWhenDutyNotFound() {
        Long dutyId = 99L;
        when(dutyRepository.findById(dutyId)).thenReturn(Optional.empty());

        assertThrows(DutyNotFoundException.class, () -> dutyService.getDuty(dutyId));
    }
}
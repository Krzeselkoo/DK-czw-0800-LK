package com.clinic.management.service;

import com.clinic.management.exception.DoctorNotFoundException;
import com.clinic.management.exception.DutyConflictException;
import com.clinic.management.exception.ExamRoomNotFoundException;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.DutyRepository;
import com.clinic.management.repository.ExamRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DutyManagementServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ExamRoomRepository examRoomRepository;

    @Mock
    private DutyRepository dutyRepository;

    @InjectMocks
    private DutyManagementService dutyManagementService;

    @Test
    void shouldDeleteDoctorWhenExistsAndNoDuties() {
        long doctorId = 1L;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(dutyRepository.existsByDoctorId(doctorId)).thenReturn(false);

        assertDoesNotThrow(() -> dutyManagementService.deleteDoctor(doctorId));
        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    void shouldThrowExceptionWhenDoctorHasDuties() {
        long doctorId = 1L;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(dutyRepository.existsByDoctorId(doctorId)).thenReturn(true);

        assertThrows(DutyConflictException.class, () -> dutyManagementService.deleteDoctor(doctorId));
        verify(doctorRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenDoctorDoesNotExistOnDelete() {
        long doctorId = 42L;
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        assertThrowsExactly(DoctorNotFoundException.class, () -> dutyManagementService.deleteDoctor(doctorId));
    }

    @Test
    void shouldDeleteExamRoomWhenNoDuties() {
        long id = 1L;
        when(examRoomRepository.existsById(id)).thenReturn(true);
        when(dutyRepository.existsByExamRoomId(id)).thenReturn(false);

        assertDoesNotThrow(() -> dutyManagementService.deleteExamRoom(id));
        verify(examRoomRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingRoomWithDuties() {
        long id = 1L;
        when(examRoomRepository.existsById(id)).thenReturn(true);
        when(dutyRepository.existsByExamRoomId(id)).thenReturn(true);

        assertThrows(DutyConflictException.class, () -> dutyManagementService.deleteExamRoom(id));
        verify(examRoomRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentRoom() {
        long id = 99L;
        when(examRoomRepository.existsById(id)).thenReturn(false);

        assertThrows(ExamRoomNotFoundException.class, () -> dutyManagementService.deleteExamRoom(id));
    }
}

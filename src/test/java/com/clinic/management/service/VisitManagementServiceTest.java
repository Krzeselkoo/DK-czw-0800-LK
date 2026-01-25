package com.clinic.management.service;

import com.clinic.management.dto.VisitRequest;
import com.clinic.management.exception.DutyNotFoundException;
import com.clinic.management.exception.PatientNotFoundException;
import com.clinic.management.exception.VisitConflictException;
import com.clinic.management.model.entity.*;
import com.clinic.management.repository.DutyRepository;
import com.clinic.management.repository.PatientRepository;
import com.clinic.management.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitManagementServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DutyRepository dutyRepository;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitManagementService visitManagementService;

    @Test
    void shouldAddVisitSuccessfully() {
        // Given
        Long patientId = 1L;
        Long dutyId = 2L;
        LocalDateTime startDate = LocalDateTime.of(2026, 5, 20, 10, 0);
        VisitRequest request = new VisitRequest(patientId, dutyId, startDate);

        // Używamy mocków zamiast builderów, aby ominąć wymóg wypełniania pól @NonNull (imię, pesel, itd.)
        Patient patient = mock(Patient.class);
        Duty duty = mock(Duty.class);

        // Konfiguracja zachowania mocków
        when(duty.getId()).thenReturn(dutyId); // Ważne: serwis używa duty.getId()

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(dutyRepository.findById(dutyId)).thenReturn(Optional.of(duty));

        // Symulujemy, że termin jest wolny (brak wizyty)
        when(visitRepository.findVisitByDutyIdAndStartDateWithLock(dutyId, startDate)).thenReturn(Optional.empty());

        // Symulujemy zapis
        when(visitRepository.save(any(Visit.class))).thenAnswer(inv -> {
            Visit v = inv.getArgument(0);
            v.setId(100L); // Symulujemy nadanie ID przez bazę
            return v;
        });

        // When
        Long resultId = visitManagementService.addVisit(request);

        // Then
        assertEquals(100L, resultId);
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void shouldThrowExceptionWhenVisitSlotIsTaken() {
        // Given
        Long patientId = 1L;
        Long dutyId = 2L;
        LocalDateTime startDate = LocalDateTime.of(2026, 5, 20, 10, 0);
        VisitRequest request = new VisitRequest(patientId, dutyId, startDate);

        Patient patient = mock(Patient.class);
        Duty duty = mock(Duty.class);
        when(duty.getId()).thenReturn(dutyId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(dutyRepository.findById(dutyId)).thenReturn(Optional.of(duty));

        // Symulujemy konflikt - metoda repozytorium zwraca istniejącą wizytę (mock)
        when(visitRepository.findVisitByDutyIdAndStartDateWithLock(dutyId, startDate))
                .thenReturn(Optional.of(mock(Visit.class)));

        // When & Then
        assertThrows(VisitConflictException.class, () -> visitManagementService.addVisit(request));
        verify(visitRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        VisitRequest request = new VisitRequest(1L, 2L, LocalDateTime.now());
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> visitManagementService.addVisit(request));
    }

    @Test
    void shouldThrowExceptionWhenDutyNotFound() {
        VisitRequest request = new VisitRequest(1L, 2L, LocalDateTime.now());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mock(Patient.class)));
        when(dutyRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(DutyNotFoundException.class, () -> visitManagementService.addVisit(request));
    }

    @Test
    void shouldGenerateAvailableHours() {
        // Given
        Long patientId = 1L;
        Long doctorId = 2L;
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 8, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 1, 10, 0);

        // Tutaj potrzebujemy "prawdziwego" obiektu Duty, aby zawierał daty
        // Używamy buildera, ale musimy podać mocki dla pól wymaganych (@NonNull)
        Duty duty = Duty.builder()
                .doctor(mock(Doctor.class))
                .examRoom(mock(ExamRoom.class))
                .fromDate(LocalDateTime.of(2026, 6, 1, 8, 0))
                .toDate(LocalDateTime.of(2026, 6, 1, 9, 0))
                .build();
        duty.setId(10L); // Ustawiamy ID setterem, bo builder go nie ma

        when(dutyRepository.findDutiesByDoctorIdAndDateRange(doctorId, from, to))
                .thenReturn(List.of(duty));

        // Zakładamy brak innych wizyt w tym czasie
        when(visitRepository.findVisitsByPatientIdAndDateRange(patientId, from, to))
                .thenReturn(Collections.emptyList());
        when(visitRepository.findVisitsByDoctorIdAndDateRange(doctorId, from, to))
                .thenReturn(Collections.emptyList());

        // When
        Map<LocalDateTime, Long> slots = visitManagementService.generateAvailableHoursForVisit(patientId, doctorId, from, to);

        // Then
        assertFalse(slots.isEmpty());
        // Dyżur trwa od 8:00 do 9:00, wizyty co 15 min -> 8:00, 8:15, 8:30, 8:45
        assertTrue(slots.containsKey(LocalDateTime.of(2026, 6, 1, 8, 0)));
        assertTrue(slots.containsKey(LocalDateTime.of(2026, 6, 1, 8, 15)));
        assertEquals(10L, slots.get(LocalDateTime.of(2026, 6, 1, 8, 0)));
    }

    @Test
    void shouldDeletePatientSuccessfully() {
        long patientId = 1L;
        when(patientRepository.existsById(patientId)).thenReturn(true);
        when(visitRepository.existsByPatientId(patientId)).thenReturn(false);

        visitManagementService.deletePatient(patientId);

        verify(patientRepository).deleteById(patientId);
    }

    @Test
    void shouldThrowConflictWhenDeletingPatientWithVisits() {
        long patientId = 1L;
        when(patientRepository.existsById(patientId)).thenReturn(true);
        when(visitRepository.existsByPatientId(patientId)).thenReturn(true);

        assertThrows(VisitConflictException.class, () -> visitManagementService.deletePatient(patientId));
        verify(patientRepository, never()).deleteById(anyLong());
    }
}
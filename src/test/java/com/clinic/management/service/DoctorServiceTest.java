package com.clinic.management.service;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.model.Doctor;
import com.clinic.management.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void shouldAddDoctorSuccessfully() {
        // given
        DoctorRequest request = new DoctorRequest(
                "Jan", "Kowalski", "12345678901", "Kardiolog", "Warszawa"
        );

        // when
        doctorService.addDoctor(request);

        // then
        verify(doctorRepository, times(1)).save(any(Doctor.class));

        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor savedDoctor = doctorCaptor.getValue();
        assertEquals("Jan", savedDoctor.getFirstName());
        assertEquals("Kardiolog", savedDoctor.getSpecialization());
    }

    @Test
    void shouldGetAllDoctorsAsSummary() {
        // given
        Doctor doctor1 = new Doctor(1L, "Jan", "Kowalski", "123", "Kardiolog", "Wawa");
        Doctor doctor2 = new Doctor(2L, "Anna", "Nowak", "456", "Dermatolog", "Kraków");

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));

        // when
        List<DoctorSummaryResponse> result = doctorService.getAllDoctors();

        // then
        assertEquals(2, result.size());
        assertEquals("Jan", result.get(0).firstName());
        assertEquals("Kardiolog", result.get(0).specialization());
    }

    @Test
    void shouldGetDoctorByIdWhenExists() {
        // given
        long doctorId = 1L;
        Doctor doctor = new Doctor(1L, "Jan", "Kowalski", "123", "Kardiolog", "Wawa");
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // when
        Doctor result = doctorService.getDoctor(doctorId);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getFirstName());
    }

    @Test
    void shouldReturnNullWhenDoctorNotFound() {
        // given
        long doctorId = 99L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // when
        Doctor result = doctorService.getDoctor(doctorId);

        // then
        assertNull(result);
    }

    @Test
    void shouldDeleteDoctorWhenExistsAndReturnTrue() {
        // given
        long doctorId = 1L;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);

        // when
        boolean deleted = doctorService.deleteDoctor(doctorId);

        // then
        assertTrue(deleted);
    }

    @Test
    void shouldReturnFalseWhenDoctorDoesNotExistOnDelete() {
        // given
        long doctorId = 42L;
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        // when
        boolean deleted = doctorService.deleteDoctor(doctorId);

        // then
        assertFalse(deleted);
    }

    @Test
    void shouldReturnFalseWhenDeleteThrowsException() {
        // given
        long doctorId = 5L;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(doctorRepository).deleteById(doctorId);

        // when
        boolean deleted = doctorService.deleteDoctor(doctorId);

        // then
        assertFalse(deleted);
    }
}
package com.clinic.management.service;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.exception.DoctorNotFoundException;
import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.util.DoctorSpecialization;
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
                "Jan", "Kowalski", "12345678901", DoctorSpecialization.OTOLARYNGOLOGIST, "Warszawa"
        );

        // when
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        long id = doctorService.addDoctor(request);

        // then
        assertEquals(1L, id);
        verify(doctorRepository, times(1)).save(any(Doctor.class));

        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor savedDoctor = doctorCaptor.getValue();
        assertEquals("Jan", savedDoctor.getFirstName());
        assertEquals(DoctorSpecialization.OTOLARYNGOLOGIST, savedDoctor.getSpecialization());
    }

    @Test
    void shouldGetAllDoctorsAsSummary() {
        // given
        Doctor doctor1 = new Doctor("Jan", "Kowalski", "12312312312", DoctorSpecialization.OTOLARYNGOLOGIST, "Wawa");
        Doctor doctor2 = new Doctor("Anna", "Nowak", "45645645645", DoctorSpecialization.NEUROLOGIST, "Kraków");

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));

        // when
        List<DoctorSummaryResponse> result = doctorService.getAllDoctors();

        // then
        assertEquals(2, result.size());
        assertEquals("Jan", result.get(0).firstName());
        assertEquals(DoctorSpecialization.OTOLARYNGOLOGIST, result.get(0).specialization());
    }

    @Test
    void shouldGetDoctorByIdWhenExists() {
        // given
        long doctorId = 1L;
        Doctor doctor = new Doctor("Jan", "Kowalski", "12312312312", DoctorSpecialization.OTOLARYNGOLOGIST, "Wawa");
        doctor.setId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // when
        DoctorSummaryResponse result = doctorService.getDoctor(doctorId);

        // then
        assertNotNull(result);
        assertEquals(doctorId, result.id());
        assertEquals("Jan", result.firstName());
    }

    @Test
    void shouldReturnNullWhenDoctorNotFound() {
        // given
        long doctorId = 99L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctor(doctorId));

    }

    @Test
    void shouldDeleteDoctorWhenExistsAndNotThrowException() {
        // given
        long doctorId = 1L;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> doctorService.deleteDoctor(doctorId));
    }

    @Test
    void shouldThrowExceptionWhenDoctorDoesNotExistOnDelete() {
        // given
        long doctorId = 42L;
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        // when & then

        assertThrowsExactly(DoctorNotFoundException.class, () -> doctorService.deleteDoctor(doctorId));
    }
}
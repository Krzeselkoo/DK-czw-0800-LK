package com.clinic.management.service;

import com.clinic.management.dto.DoctorRequest;
import com.clinic.management.dto.DoctorSummaryResponse;
import com.clinic.management.exception.DoctorNotFoundException;
import com.clinic.management.exception.DutyConflictException;
import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.util.DoctorSpecialization;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.DutyRepository;
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

    @Mock
    private DutyRepository dutyRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void shouldAddDoctorSuccessfully() {
        DoctorRequest request = new DoctorRequest(
                "Jan", "Kowalski", "12345678901", DoctorSpecialization.OTOLARYNGOLOGIST, "Warszawa"
        );

        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        long id = doctorService.addDoctor(request);

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
        Doctor doctor1 = Doctor.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .pesel("12312312312")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address( "Wawa")
                .build();

        Doctor doctor2 = Doctor.builder()
                .firstName("Anna")
                .lastName("Nowak")
                .pesel("45645645645")
                .specialization(DoctorSpecialization.NEUROLOGIST)
                .address("Kraków")
                .build();

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));

        List<DoctorSummaryResponse> result = doctorService.getAllDoctors();

        assertEquals(2, result.size());
        assertEquals("Jan", result.get(0).firstName());
        assertEquals(DoctorSpecialization.OTOLARYNGOLOGIST, result.get(0).specialization());
    }

    @Test
    void shouldGetDoctorByIdWhenExists() {
        long doctorId = 1L;
        Doctor doctor = Doctor.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .pesel("12312312312")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address("Wawa")
                .build();

        doctor.setId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        DoctorSummaryResponse result = doctorService.getDoctor(doctorId);

        assertNotNull(result);
        assertEquals(doctorId, result.id());
        assertEquals("Jan", result.firstName());
    }

    @Test
    void shouldReturnNullWhenDoctorNotFound() {
        long doctorId = 99L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctor(doctorId));
    }
}
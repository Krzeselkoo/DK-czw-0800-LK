package com.clinic.management.service;

import com.clinic.management.dto.PatientRequest;
import com.clinic.management.dto.PatientSummaryResponse;
import com.clinic.management.exception.DuplicatePeselException;
import com.clinic.management.exception.PatientNotFoundException;
import com.clinic.management.model.entity.Patient;
import com.clinic.management.repository.PatientRepository;
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
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldAddPatientSuccessfully() {
        PatientRequest request = new PatientRequest(
                "Adam", "Nowak", "90010112345", "Krakow"
        );

        when(patientRepository.existsByPesel(request.getPesel())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        long id = patientService.addPatient(request);

        assertEquals(1L, id);
        verify(patientRepository, times(1)).save(any(Patient.class));

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());
        Patient savedPatient = patientCaptor.getValue();
        assertEquals("Adam", savedPatient.getFirstName());
        assertEquals("90010112345", savedPatient.getPesel());
    }

    @Test
    void shouldThrowExceptionWhenAddingPatientWithDuplicatePesel() {
        PatientRequest request = new PatientRequest(
                "Adam", "Nowak", "90010112345", "Krakow"
        );

        when(patientRepository.existsByPesel(request.getPesel())).thenReturn(true);

        assertThrows(DuplicatePeselException.class, () -> patientService.addPatient(request));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void shouldGetAllPatientsAsSummary() {
        Patient p1 = Patient.builder()
                .firstName("Adam")
                .lastName("Nowak")
                .pesel("90010112345")
                .address("Krakow")
                .build();

        Patient p2 = Patient.builder()
                .firstName("Ewa")
                .lastName("Kowalska")
                .pesel("80020212345")
                .address("Warszawa")
                .build();

        when(patientRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PatientSummaryResponse> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        assertEquals("Adam", result.get(0).firstName());
        assertEquals("Ewa", result.get(1).firstName());
    }

    @Test
    void shouldGetPatientByIdWhenExists() {
        long id = 1L;
        Patient p = Patient.builder()
                .firstName("Adam")
                .lastName("Nowak")
                .pesel("90010112345")
                .address("Krakow")
                .build();
        p.setId(id);
        p.setId(id);

        when(patientRepository.findById(id)).thenReturn(Optional.of(p));

        PatientSummaryResponse result = patientService.getPatient(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Adam", result.firstName());
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        long id = 99L;
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatient(id));
    }

    @Test
    void shouldDeletePatientWhenExists() {
        long id = 1L;
        when(patientRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> patientService.deletePatient(id));
        verify(patientRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPatient() {
        long id = 99L;
        when(patientRepository.existsById(id)).thenReturn(false);

        assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(id));
        verify(patientRepository, never()).deleteById(anyLong());
    }
}
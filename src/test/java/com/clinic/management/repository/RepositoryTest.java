package com.clinic.management.repository;

import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.util.DoctorSpecialization;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class RepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void contextLoads(){
        assertThat(doctorRepository).isNotNull();
    }

    @Test
    @Transactional
    void shouldAddDoctor(){
        //Given
        Doctor doctor1 = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .pesel("12345678901")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address("ABC")
                .build();

        //When
        doctorRepository.saveAndFlush(doctor1);

        //Then
        Optional<Doctor> savedDoctor = doctorRepository.findById(doctor1.getId());
        assertThat(savedDoctor).isPresent();
        assertThat(savedDoctor.get().getFirstName()).isEqualTo("John");
        assertThat(savedDoctor.get().getLastName()).isEqualTo("Doe");
        assertThat(savedDoctor.get().getPesel()).isEqualTo("12345678901");
        assertThat(savedDoctor.get().getSpecialization()).isEqualTo(DoctorSpecialization.OTOLARYNGOLOGIST);
        assertThat(savedDoctor.get().getAddress()).isEqualTo("ABC");
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenAddingDoctorsWithSamePesel() {
        // Given
        Doctor doctor1 = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .pesel("12345678901")
                .specialization(DoctorSpecialization.NEUROLOGIST)
                .address("ABC")
                .build();

        Doctor doctor2 = Doctor.builder()
                .firstName("Johna")
                .lastName("Dou")
                .pesel("12345678901")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address("CBA")
                .build();

        doctorRepository.saveAndFlush(doctor1);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorRepository.saveAndFlush(doctor2);
        });
    }
}
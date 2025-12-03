package com.clinic.management.repository;

import com.clinic.management.model.entity.Doctor;
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
    void shouldAddDoctor(){
        //Given
        Doctor doctor1 = new Doctor();
        doctor1.setFirstName("John");
        doctor1.setLastName("Doe");
        doctor1.setPESEL("12345678901");
        doctor1.setSpecialization("Cardiology");
        doctor1.setAddress("ABC");

        //When
        doctorRepository.saveAndFlush(doctor1);

        //Then
        Optional<Doctor> savedDoctor = doctorRepository.findById(doctor1.getId());
        assertThat(savedDoctor).isPresent();
        assertThat(savedDoctor.get().getFirstName()).isEqualTo("John");
        assertThat(savedDoctor.get().getLastName()).isEqualTo("Doe");
        assertThat(savedDoctor.get().getPESEL()).isEqualTo("12345678901");
        assertThat(savedDoctor.get().getSpecialization()).isEqualTo("Cardiology");
        assertThat(savedDoctor.get().getAddress()).isEqualTo("ABC");
    }

    @Test
    void shouldThrowExceptionWhenAddingDoctorsWithSamePesel() {
        // Given
        Doctor doctor1 = new Doctor();
        doctor1.setFirstName("John");
        doctor1.setLastName("Doe");
        doctor1.setPESEL("12345678901");
        doctor1.setSpecialization("Cardiology");
        doctor1.setAddress("ABC");

        Doctor doctor2 = new Doctor();
        doctor2.setFirstName("Johna");
        doctor2.setLastName("Dou");
        doctor2.setPESEL("12345678901");
        doctor2.setSpecialization("Surgeon");
        doctor2.setAddress("CBA");

        doctorRepository.saveAndFlush(doctor1);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorRepository.saveAndFlush(doctor2);
        });
    }
}
package com.clinic.management;

import com.clinic.management.model.entity.Doctor;
import com.clinic.management.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ClinicApplicationTests {

	@Autowired
	private DoctorRepository doctorRepository;

	@Test
	void shouldThrowExceptionWhenAddingDoctorsWithSamePesel() {
		// Given
		Doctor doctor1 = new Doctor();
		doctor1.setFirstName("John");
		doctor1.setLastName("Doe");
		doctor1.setPESELNumber("12345678901");
		doctor1.setSpecialization("Cardiology");
		doctor1.setAddress("ABC");


		Doctor doctor2 = new Doctor();
		doctor2.setFirstName("Johna");
		doctor2.setLastName("Dou");
		doctor2.setPESELNumber("12345678901");
		doctor2.setSpecialization("Surgeon");
		doctor2.setAddress("CBA");

		// When
		doctorRepository.save(doctor1);

		// Then
		assertThrows(DataIntegrityViolationException.class, () -> {
			doctorRepository.saveAndFlush(doctor2);
		});
	}
}

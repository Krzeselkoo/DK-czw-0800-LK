package com.clinic.management.service;

import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.entity.ExamRoom;
import com.clinic.management.model.entity.Patient;
import com.clinic.management.model.util.DoctorSpecialization;
import com.clinic.management.model.util.RoomType;
import com.clinic.management.repository.DoctorRepository;
import com.clinic.management.repository.ExamRoomRepository;
import com.clinic.management.repository.PatientRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitDataService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ExamRoomRepository examRoomRepository;

    /**
     * Populates the database with predefined dummy entities.
     * This method saves a list of entities with their details into their corresponding repository.
     */
    public void initialize() {
        initializeDoctors();
        initializePatients();
        initializeExamRooms();
    }

    private void initializeDoctors(){
        doctorRepository.save(Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .pesel("12345678901")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address("123 Main St")
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Jane")
                .lastName("Smith")
                .pesel("23456789012")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address("456 Elm St")
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .pesel("34567890123")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address("789 Oak St")
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Bob")
                .lastName("Brown")
                .pesel("45678901234")
                .specialization(DoctorSpecialization.OTOLARYNGOLOGIST)
                .address("101 Pine St")
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Charlie")
                .lastName("Davis")
                .pesel("56789012345")
                .specialization(DoctorSpecialization.NEUROLOGIST)
                .address("202 Maple St")
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Diana")
                .lastName("Wilson")
                .pesel("67890123456")
                .specialization(DoctorSpecialization.PSYCHOLOGIST)
                .address("303 Birch St")
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Eve")
                .lastName("Taylor")
                .pesel("78901234567")
                .specialization(DoctorSpecialization.NEUROLOGIST)
                .address("404 Cedar St")
                .build());
    }
    
    private void initializePatients(){
        patientRepository.save(Patient.builder()
                .firstName("Tom")
                .lastName("Harris")
                .pesel("11111111111")
                .address("12 Baker St")
                .build());

        patientRepository.save(Patient.builder()
                .firstName("Sara")
                .lastName("Connor")
                .pesel("22222222222")
                .address("34 Willow Ave")
                .build());

        patientRepository.save(Patient.builder()
                .firstName("Mark")
                .lastName("Lee")
                .pesel("33333333333")
                .address("56 Oak Lane")
                .build());

        patientRepository.save(Patient.builder()
                .firstName("Olivia")
                .lastName("Brown")
                .pesel("44444444444")
                .address("78 Pine Road")
                .build());
    }
    
    private void initializeExamRooms(){
        examRoomRepository.save(ExamRoom.builder()
                .roomCode("A-101")
                .roomType(RoomType.GENERAL)
                .build());

        examRoomRepository.save(ExamRoom.builder()
                .roomCode("B-202")
                .roomType(RoomType.SURGERY)
                .build());

        examRoomRepository.save(ExamRoom.builder()
                .roomCode("C-303")
                .roomType(RoomType.GENERAL)
                .build());
    }

    @PostConstruct
    public void databaseClean(){
        examRoomRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
    }

}

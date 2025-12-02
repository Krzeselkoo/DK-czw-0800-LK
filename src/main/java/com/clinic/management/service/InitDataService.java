package com.clinic.management.service;

import com.clinic.management.model.entity.Doctor;
import com.clinic.management.repository.DoctorRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class InitDataService {

    private final DoctorRepository doctorRepository;

    public InitDataService(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }

    public void initialize() {
        doctorRepository.save(new Doctor("John", "Doe", "12345678901", "Cardiology", "123 Main St"));
        doctorRepository.save(new Doctor("Jane", "Smith", "23456789012", "Cardiology", "456 Elm St"));
        doctorRepository.save(new Doctor("Alice", "Johnson", "34567890123", "Cardiology", "789 Oak St"));
        doctorRepository.save(new Doctor("Bob", "Brown", "45678901234", "Cardiology", "101 Pine St"));
        doctorRepository.save(new Doctor("Charlie", "Davis", "56789012345", "Dermatology", "202 Maple St"));
        doctorRepository.save(new Doctor("Diana", "Wilson", "67890123456", "Radiology", "303 Birch St"));
        doctorRepository.save(new Doctor("Eve", "Taylor", "78901234567", "Dermatology", "404 Cedar St"));
    }

    @PostConstruct
    public void databaseClean(){
        doctorRepository.deleteAll();
    }

}

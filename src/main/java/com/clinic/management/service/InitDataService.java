package com.clinic.management.service;

import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.util.DoctorSpecialization;
import com.clinic.management.repository.DoctorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class InitDataService {

    private final DoctorRepository doctorRepository;

    public InitDataService(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }

    /**
     * Populates the database with predefined doctor entities.
     * This method saves a list of doctors with their details into the repository.
     */
    public void initialize() {
        doctorRepository.save(new Doctor("John", "Doe", "12345678901", DoctorSpecialization.OTOLARYNGOLOGIST, "123 Main St"));
        doctorRepository.save(new Doctor("Jane", "Smith", "23456789012", DoctorSpecialization.OTOLARYNGOLOGIST, "456 Elm St"));
        doctorRepository.save(new Doctor("Alice", "Johnson", "34567890123", DoctorSpecialization.OTOLARYNGOLOGIST, "789 Oak St"));
        doctorRepository.save(new Doctor("Bob", "Brown", "45678901234", DoctorSpecialization.OTOLARYNGOLOGIST, "101 Pine St"));
        doctorRepository.save(new Doctor("Charlie", "Davis", "56789012345", DoctorSpecialization.NEUROLOGIST, "202 Maple St"));
        doctorRepository.save(new Doctor("Diana", "Wilson", "67890123456", DoctorSpecialization.PSYCHOLOGIST, "303 Birch St"));
        doctorRepository.save(new Doctor("Eve", "Taylor", "78901234567", DoctorSpecialization.NEUROLOGIST, "404 Cedar St"));
    }

    @PostConstruct
    public void databaseClean(){
        doctorRepository.deleteAll();
    }

}

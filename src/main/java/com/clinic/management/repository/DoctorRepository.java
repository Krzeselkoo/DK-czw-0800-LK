package com.clinic.management.repository;

import com.clinic.management.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByPesel(String pesel);

    @Query("SELECT d FROM Doctor d WHERE d.id NOT IN (" +
            "SELECT du.doctor.id FROM Duty du WHERE " +
            "(du.fromDate BETWEEN :from AND :to OR du.toDate BETWEEN :from AND :to " +
            "OR (du.fromDate < :from AND du.toDate > :to)))")
    List<Doctor> findAvailableDoctors(@Param("from") LocalDateTime fromDate, @Param("to") LocalDateTime toDate);
}

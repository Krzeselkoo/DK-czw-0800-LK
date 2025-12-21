package com.clinic.management.repository;

import com.clinic.management.model.entity.Doctor;
import com.clinic.management.model.entity.Duty;
import com.clinic.management.model.entity.ExamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public interface DutyRepository extends JpaRepository<Duty, Long> {
    boolean existsByDoctorId(Long doctorId);
    boolean existsByExamRoomId(Long examRoomId);
    List<Duty> getDutiesByDoctorId(Long doctorId);
    List<Duty> getDutiesByExamRoomId(Long id);
    @Query("SELECT " +
            "CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Duty d WHERE d.doctor = :doctor " +
            "AND (d.fromDate BETWEEN :from AND :to OR d.toDate BETWEEN :from AND :to " +
            "OR (d.fromDate < :from AND d.toDate > :to))")
    boolean existingDoctorIsBusyAtThisTime(@Param("doctor") Doctor doctor, @Param("from") LocalDate fromDate, @Param("to") LocalDate toDate);

    @Query("SELECT " +
            "CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Duty d WHERE d.examRoom = :examRoom " +
            "AND (d.fromDate BETWEEN :from AND :to OR d.toDate BETWEEN :from AND :to " +
            "OR (d.fromDate < :from AND d.toDate > :to))")
    boolean existingRoomIsOccupiedAtThisTime(@Param("examRoom") ExamRoom examRoom, @Param("from") LocalDate fromDate, @Param("to") LocalDate toDate);
}

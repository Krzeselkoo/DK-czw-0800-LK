package com.clinic.management.repository;

import com.clinic.management.model.entity.Visit;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    boolean existsByDutyId(Long dutyId);
    boolean existsByPatientId(Long patientId);
    List<Visit> findAllByPatientId(Long patientId);
    List<Visit> findAllByDutyId(Long dutyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Visit v WHERE v.duty.id = :dutyId AND v.startDate = :startDate")
    Optional<Visit> findVisitByDutyIdAndStartDateWithLock(@Param("dutyId") Long dutyId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT v FROM Visit v WHERE v.duty.doctor.id = :doctorId")
    List<Visit> findAllByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT v FROM Visit v WHERE v.patient.id = :patientId AND v.startDate >= :from AND v.startDate <= :to")
    List<Visit> findVisitsByPatientIdAndDateRange(@Param("patientId") Long patientId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT v FROM Visit v WHERE v.duty.doctor.id = :doctorId AND v.startDate >= :from AND v.startDate <= :to")
    List<Visit> findVisitsByDoctorIdAndDateRange(@Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}

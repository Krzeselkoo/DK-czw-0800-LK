package com.clinic.management.repository;

import com.clinic.management.model.entity.ExamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRoomRepository extends JpaRepository<ExamRoom, Long> {
    boolean existsByRoomCode(String roomCode);

    @Query("SELECT er FROM ExamRoom er WHERE er.id NOT IN (" +
            "SELECT d.examRoom.id FROM Duty d WHERE " +
            "(d.fromDate BETWEEN :from AND :to OR d.toDate BETWEEN :from AND :to " +
            "OR (d.fromDate < :from AND d.toDate > :to)))")
    List<ExamRoom> findAvailableRooms(@Param("from") LocalDateTime fromDate, @Param("to") LocalDateTime toDate);
}

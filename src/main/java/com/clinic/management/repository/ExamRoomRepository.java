package com.clinic.management.repository;

import com.clinic.management.model.entity.ExamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRoomRepository extends JpaRepository<ExamRoom, Long> {
    boolean existsByRoomCode(String roomCode);
}

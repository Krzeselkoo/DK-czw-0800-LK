package com.clinic.management.model.entity;

import com.clinic.management.model.util.RoomType;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamRoom {
    @Id
    @GeneratedValue
    private Long id;

    private String roomCode;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Builder
    private ExamRoom(@NonNull String roomCode, @NonNull RoomType roomType) {
        this.roomCode = roomCode;
        this.roomType = roomType;
    }
}

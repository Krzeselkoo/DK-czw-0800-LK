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
    public Long id;

    public String roomCode;

    @Enumerated(EnumType.STRING)
    public RoomType roomType;

    @Builder
    private ExamRoom(@NonNull String roomCode, @NonNull RoomType roomType) {
        this.roomCode = roomCode;
        this.roomType = roomType;
    }
}

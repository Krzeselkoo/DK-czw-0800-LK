package com.clinic.management.model.entity;

import com.clinic.management.model.util.RoomType;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ExamRoom {
    @Id
    @GeneratedValue
    public Long id;

    @NonNull
    public String roomCode;

    @NonNull
    @Enumerated(EnumType.STRING)
    public RoomType roomType;
}

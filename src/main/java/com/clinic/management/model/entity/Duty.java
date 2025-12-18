package com.clinic.management.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Duty {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne
    public Doctor doctor;

    @ManyToOne
    public ExamRoom examRoom;

    public LocalDate fromDate;
    public LocalDate toDate;

    @Builder
    private Duty(@NonNull Doctor doctor, @NonNull ExamRoom examRoom, @NonNull LocalDate fromDate, @NonNull LocalDate toDate) {
        this.doctor = doctor;
        this.examRoom = examRoom;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}

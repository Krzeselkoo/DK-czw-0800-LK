package com.clinic.management.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Duty {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne
    @NonNull
    public Doctor doctor;

    @ManyToOne
    @NonNull
    public ExamRoom examRoom;

    @NonNull
    public LocalDate fromDate;

    @NonNull
    public LocalDate toDate;
}

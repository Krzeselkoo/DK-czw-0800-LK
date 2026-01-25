package com.clinic.management.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Visit {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Duty duty;

    private LocalDateTime startDate;

    @Builder
    private Visit(@NonNull Patient patient, @NonNull Duty duty, @NonNull LocalDateTime startDate) {
        this.patient = patient;
        this.duty = duty;
        this.startDate = startDate;
    }
}

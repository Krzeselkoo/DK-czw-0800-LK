package com.clinic.management.model.entity;

import com.clinic.management.model.util.DoctorSpecialization;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    @Column(unique = true)
    private String PESEL;

    @NonNull
    @Enumerated(EnumType.STRING)
    private DoctorSpecialization specialization;

    @NonNull
    private String address;
}

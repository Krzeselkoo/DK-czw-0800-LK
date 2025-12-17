package com.clinic.management.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Patient {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    @Column(unique = true)
    private String pesel;

    @NonNull
    private String address;
}
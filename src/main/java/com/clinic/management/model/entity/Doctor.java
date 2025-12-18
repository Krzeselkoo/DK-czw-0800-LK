package com.clinic.management.model.entity;

import com.clinic.management.model.util.DoctorSpecialization;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends Person{
    @NonNull
    @Enumerated(EnumType.STRING)
    private DoctorSpecialization specialization;
}

package com.tejasvi.trainer_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trainer {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="name")
    private String name;
    @Column(name="speciality")
    private String speciality;
    @Column(name="certificate")
    private String certificate;
    @Column(name="experience_years")
    private int experienceYears;
    @Column(name="phone_number")
    private String phoneNumber;
    @Column(name="workout_plan")
    private String workoutPlan;
}

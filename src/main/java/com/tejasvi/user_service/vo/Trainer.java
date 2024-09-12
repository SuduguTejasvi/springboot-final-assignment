package com.tejasvi.user_service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {
    private int id;

    private String name;

    private String speciality;

    private String certificate;

    private int experienceYears;

    private String phoneNumber;

    private String workoutPlan;
}

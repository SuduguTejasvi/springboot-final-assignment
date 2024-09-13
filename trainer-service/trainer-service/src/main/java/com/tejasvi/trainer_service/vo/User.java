package com.tejasvi.trainer_service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    enum Level {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }
    private int id;
    private String name;
    private int age;
    private Level fitnesslessLevel;
    private Long trainerId;
}

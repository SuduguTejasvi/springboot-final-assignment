package com.tejasvi.trainer_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {
    private long id;
    @NotNull
    @Size(max=30, message = "Name cannot exceed 30 characters")
    private String name;
    @NotNull
    @Size(max=30, message = "Speciality cannot exceed 30 characters")
    private String speciality;
    @NotNull
    @Size(max=30, message = "Certificate cannot exceed 30 characters")
    private String certificate;
    @NotNull
    private int experienceYears;
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number, must be 10 digits")
    private String phoneNumber;
    @NotNull
    @Size(max=45, message = "Workout plan cannot exceed 45 characters")
    private String workoutPlan;
}

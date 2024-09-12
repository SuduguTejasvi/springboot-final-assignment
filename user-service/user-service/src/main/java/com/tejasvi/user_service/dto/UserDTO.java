package com.tejasvi.user_service.dto;

import com.tejasvi.user_service.enums.Level;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @NotNull
    @Size(min=3,max=30,message = "Name should contain minimum of 3 characters and maximum of 30 characters")
    private String name;

    @NotNull
    @Min(value = 18,message = "Minimum age should be 18")
    private int age;

    @Pattern(regexp = "^[A-Za-z0-9]+@[A-Za-z]+\\.[a-z]{2,}$",message ="Invalid email address")
    private String email;

    @NotNull
//    @Pattern(regexp = "BEGINNER|INTERMEDIATE|ADVANCED", message = "Invalid fitness level value")
    private Level fitnessLevel;

    private Long trainerId;
}

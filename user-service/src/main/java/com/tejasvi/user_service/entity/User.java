package com.tejasvi.user_service.entity;

import com.tejasvi.user_service.enums.Level;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="age")
    private int age;

    @Column(name="email")
    private String email;

    @Column(name="fitness_level")
    private Level fitnessLevel;

    @Column(name="trainer_id")
    private Long trainerId;
}

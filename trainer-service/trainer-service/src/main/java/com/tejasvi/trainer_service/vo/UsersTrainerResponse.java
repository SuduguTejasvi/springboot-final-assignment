package com.tejasvi.trainer_service.vo;

import com.tejasvi.trainer_service.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersTrainerResponse {
    private Trainer trainer;
    private List<User> user;
}

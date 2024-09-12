package com.tejasvi.user_service.vo;

import com.tejasvi.user_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTrainerResponse {

    private User user;
    private Trainer trainer;

}

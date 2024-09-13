package com.tejasvi.trainer_service.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrainerErrorResponse {

    private int status;
    private String message;

}

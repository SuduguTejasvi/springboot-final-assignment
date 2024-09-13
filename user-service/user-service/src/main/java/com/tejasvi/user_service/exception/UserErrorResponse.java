package com.tejasvi.user_service.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserErrorResponse {
    private int status;
    private String message;
}

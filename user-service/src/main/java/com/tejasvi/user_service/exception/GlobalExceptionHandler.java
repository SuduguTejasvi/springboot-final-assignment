package com.tejasvi.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        UserErrorResponse response=new UserErrorResponse(HttpStatus.NOT_FOUND.value(), "user not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<UserErrorResponse> handleInvalidInputException(InvalidInputException ex) {
        UserErrorResponse response = new UserErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid input provided");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserErrorResponse> handleGlobalException(Exception ex) {
        UserErrorResponse response=new UserErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server failure");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

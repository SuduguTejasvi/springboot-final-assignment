package com.tejasvi.trainer_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<TrainerErrorResponse> handleUserNotFoundException(TrainerNotFoundException e){
        TrainerErrorResponse response=new TrainerErrorResponse(HttpStatus.NOT_FOUND.value(), "Trainer not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<TrainerErrorResponse> handleInvalidInputException(InvalidInputException e){
        TrainerErrorResponse response=new TrainerErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid input provided");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<TrainerErrorResponse> handleGlobalException(Exception e){
        TrainerErrorResponse response=new TrainerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server failure");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

package com.tejasvi.trainer_service.exception;

public class TrainerNotFoundException extends RuntimeException{
    public TrainerNotFoundException(String msg){
        super(msg);
    }
}

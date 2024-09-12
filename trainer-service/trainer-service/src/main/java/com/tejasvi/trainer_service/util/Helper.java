package com.tejasvi.trainer_service.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public class Helper {

    public String handleBindingErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((message1, message2) -> message1 + "," + message2)
                    .orElse("Invalid details provided");
            return errorMessage;
        }
        return null;
    }
}
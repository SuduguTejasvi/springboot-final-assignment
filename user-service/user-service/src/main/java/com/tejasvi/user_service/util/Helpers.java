package com.tejasvi.user_service.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class Helpers {

    public String validateUserRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((message1, message2) -> message1 + "," + message2)
                    .orElse("Invalid details provided");
        }
        return null;
    }
}

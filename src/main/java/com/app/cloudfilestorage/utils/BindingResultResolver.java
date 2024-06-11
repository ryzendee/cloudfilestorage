package com.app.cloudfilestorage.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class BindingResultResolver {

    private static final String DEFAULT_MESSAGE = "Something went wrong...";

    public static String getFirstMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse(DEFAULT_MESSAGE);
    }
}

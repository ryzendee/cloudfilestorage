package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.exception.SignupException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(SignupException.class)
    public RedirectView handleSignUpException(SignupException ex,
                                              RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("exceptionMessage", ex.getMessage());
        return new RedirectView("/signup");
    }
}

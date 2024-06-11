package com.app.cloudfilestorage.validation;

import com.app.cloudfilestorage.dto.request.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatcherValidator implements ConstraintValidator<PasswordMatcher, SignupRequest> {

    @Override
    public void initialize(PasswordMatcher constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SignupRequest value, ConstraintValidatorContext context) {
        return value.getPassword() != null && value.getPassword().equals(value.getPasswordConfirmation());
    }
}
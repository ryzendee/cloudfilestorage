package com.app.cloudfilestorage.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FlashAttr {
    VALIDATION_ERROR_MESSAGE("validationErrorMessage"),
    EXCEPTION_ERROR_MESSAGE("exceptionErrorMessage"),
    SUCCESS_MESSAGE("successMessage");

    private final String name;
}

package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.exception.FileServiceException;
import com.app.cloudfilestorage.exception.FolderServiceException;
import com.app.cloudfilestorage.exception.SignupException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    private static final String FLASH_ATR_EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String HOME_PAGE_URI = "/";

    @ExceptionHandler(SignupException.class)
    public RedirectView handleSignUpEx(SignupException ex,
                                       RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(FLASH_ATR_EXCEPTION_MESSAGE, ex.getMessage());
        return new RedirectView("/signup");
    }

    @ExceptionHandler(FolderServiceException.class)
    public RedirectView handleFolderServiceEx(FolderServiceException ex,
                                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FLASH_ATR_EXCEPTION_MESSAGE, ex.getMessage());
        return new RedirectView(HOME_PAGE_URI);
    }

    @ExceptionHandler(FileServiceException.class)
    public RedirectView handleFileServiceEx(FileServiceException ex,
                                            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(FLASH_ATR_EXCEPTION_MESSAGE, ex.getMessage());
        return new RedirectView(HOME_PAGE_URI);
    }
}

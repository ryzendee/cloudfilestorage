package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.request.FolderCreateRequest;
import com.app.cloudfilestorage.dto.request.FolderUploadRequest;
import com.app.cloudfilestorage.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.app.cloudfilestorage.utils.BindingResultResolver.getFirstMessage;

@Controller
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {
    private static final String HOME_PAGE_URI = "/";
    private static final String VALIDATION_ERROR_MESSAGE = "validationErrorMessage";
    private final FolderService folderService;

    @PostMapping()
    public RedirectView uploadFolder(@Valid @ModelAttribute FolderUploadRequest folderUploadRequest,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.uploadFolder(folderUploadRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Folder created successfully!");
        }

        return new RedirectView(HOME_PAGE_URI);

    }

    @PostMapping("/empty")
    public RedirectView createEmptyFolder(@Valid @ModelAttribute FolderCreateRequest folderCreateRequest,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.createEmptyFolder(folderCreateRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Folder created successfully!");
        }

        return new RedirectView(HOME_PAGE_URI);
    }
}

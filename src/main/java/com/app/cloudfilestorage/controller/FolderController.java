package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.dto.request.FolderCreateRequest;
import com.app.cloudfilestorage.dto.request.FolderDeleteRequest;
import com.app.cloudfilestorage.dto.request.FolderDownloadRequest;
import com.app.cloudfilestorage.dto.request.FolderUploadRequest;
import com.app.cloudfilestorage.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.app.cloudfilestorage.utils.BindingResultResolver.getFirstMessage;

@Controller
@RequestMapping("/folders")
@Slf4j
@RequiredArgsConstructor
public class FolderController {
    private static final String HOME_PAGE_URI = "/";
    private static final String VALIDATION_ERROR_MESSAGE = "validationErrorMessage";
    private final FolderService folderService;

    @PostMapping("/upload")
    public RedirectView uploadFolder(@Valid @ModelAttribute FolderUploadRequest folderUploadRequest,
                                     BindingResult bindingResult,
                                     @SessionAttribute UserSessionDto userSessionDto,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.uploadFolder(userSessionDto.id(), folderUploadRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Folder created successfully!");
        }

        return new RedirectView(HOME_PAGE_URI);
    }

    @PostMapping("/empty")
    public RedirectView createEmptyFolder(@Valid @ModelAttribute FolderCreateRequest folderCreateRequest,
                                          BindingResult bindingResult,
                                          @SessionAttribute UserSessionDto userSessionDto,
                                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.createEmptyFolder(userSessionDto.id(), folderCreateRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Folder created successfully!");
        }

        return new RedirectView(HOME_PAGE_URI);
    }

    @PostMapping("/delete")
    public RedirectView deleteFolder(@Valid @ModelAttribute FolderDeleteRequest folderDeleteRequest,
                                     BindingResult bindingResult,
                                     @SessionAttribute UserSessionDto userSessionDto,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.deleteFolder(userSessionDto.id(), folderDeleteRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Folder was deleted successfully");
        }
        return new RedirectView(HOME_PAGE_URI);
    }


    @PostMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Object downloadFolder(@Valid @ModelAttribute FolderDownloadRequest folderDownloadRequest,
                                 BindingResult bindingResult,
                                 @SessionAttribute UserSessionDto userSessionDto,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
            return new RedirectView(HOME_PAGE_URI);
        } else {
            Resource folderResource = folderService.downloadFolder(userSessionDto.id(), folderDownloadRequest);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + folderDownloadRequest.getName() + ".zip")
                    .body(folderResource);
        }
    }
}

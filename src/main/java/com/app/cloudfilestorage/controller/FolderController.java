package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.request.folder.*;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.ByteArrayOutputStream;

import static com.app.cloudfilestorage.utils.BindingResultResolver.getFirstMessage;

@Controller
@RequestMapping("/folders")
@Slf4j
@RequiredArgsConstructor
public class FolderController {
    private static final String HOME_PAGE_URI = "/";
    private static final String FLASH_ATR_VALIDATION_ERROR_MESSAGE = "validationErrorMessage";
    private static final String FLASH_ATR_SUCCESS_MESSAGE = "successMessage";
    private final FolderService folderService;

    @PostMapping("/upload")
    public RedirectView uploadFolder(@Valid @ModelAttribute FolderUploadRequest folderUploadRequest,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserEntity currentUser,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.uploadFolder(currentUser.getId(), folderUploadRequest);
            redirectAttributes.addFlashAttribute(FLASH_ATR_SUCCESS_MESSAGE, "Folder created successfully!");
        }

        return new RedirectView(HOME_PAGE_URI);
    }

    @PostMapping("/empty")
    public RedirectView createEmptyFolder(@Valid @ModelAttribute FolderCreateRequest folderCreateRequest,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal UserEntity currentUser,
                                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.createEmptyFolder(currentUser.getId(), folderCreateRequest);
            redirectAttributes.addFlashAttribute(FLASH_ATR_SUCCESS_MESSAGE, "Folder created successfully!");
        }

        return new RedirectView(HOME_PAGE_URI);
    }

    @PostMapping("/delete")
    public RedirectView deleteFolder(@Valid @ModelAttribute FolderDeleteRequest folderDeleteRequest,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserEntity currentUser,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.deleteFolder(currentUser.getId(), folderDeleteRequest);
            redirectAttributes.addFlashAttribute(FLASH_ATR_SUCCESS_MESSAGE, "Folder was deleted successfully");
        }

        return new RedirectView(HOME_PAGE_URI);
    }


    @PostMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Object downloadFolder(@Valid @ModelAttribute FolderDownloadRequest folderDownloadRequest,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserEntity currentUser,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
            return new RedirectView(HOME_PAGE_URI);
        } else {
            ByteArrayOutputStream baos = folderService.downloadFolder(currentUser.getId(), folderDownloadRequest);
            Resource folderResource = new ByteArrayResource(baos.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + folderDownloadRequest.getName() + ".zip")
                    .body(folderResource);
        }
    }

    @PostMapping("/rename")
    public RedirectView renameFolder(@Valid @ModelAttribute FolderRenameRequest folderRenameRequest,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserEntity currentUser,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            folderService.renameFolder(currentUser.getId(), folderRenameRequest);
            redirectAttributes.addAttribute(FLASH_ATR_SUCCESS_MESSAGE, "Folder was renamed successfully");
        }

        return new RedirectView(HOME_PAGE_URI);
    }
}

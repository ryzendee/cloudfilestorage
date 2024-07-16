package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.request.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.FileRenameRequest;
import com.app.cloudfilestorage.dto.request.FileUploadRequest;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.nio.charset.StandardCharsets;

import static com.app.cloudfilestorage.utils.BindingResultResolver.getFirstMessage;
import static org.springframework.web.util.UriUtils.encode;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private static final String HOME_PAGE_URI = "/";
    private static final String FLASH_ATR_VALIDATION_ERROR_MESSAGE = "validationErrorMessage";
    private static final String FLASH_ATR_SUCCESS_MESSAGE = "successMessage";
    private final FileService fileService;

    @PostMapping()
    public RedirectView uploadFile(@Valid @ModelAttribute FileUploadRequest fileUploadRequest,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserEntity currentUser,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            fileService.uploadFile(currentUser.getId(), fileUploadRequest);
            redirectAttributes.addAttribute(FLASH_ATR_SUCCESS_MESSAGE, "File was uploaded!");
        }

        return new RedirectView(HOME_PAGE_URI);
    }

    @PostMapping("/delete")
    public RedirectView deleteFile(@Valid @ModelAttribute FileDeleteRequest fileDeleteRequest,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserEntity currentUser,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            fileService.deleteFile(currentUser.getId(), fileDeleteRequest);
            redirectAttributes.addAttribute(FLASH_ATR_SUCCESS_MESSAGE, "File was deleted!");
        }

        return new RedirectView(HOME_PAGE_URI);

    }

    @PostMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Object downloadFile(@Valid @ModelAttribute FileDownloadRequest fileDownloadRequest,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserEntity currentUser,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
            return new RedirectView(HOME_PAGE_URI);
        } else {
            redirectAttributes.addAttribute(FLASH_ATR_SUCCESS_MESSAGE, "Starts file downloading...");
            Resource fileResource = fileService.downloadFile(currentUser.getId(), fileDownloadRequest);
            String filename = encode(fileDownloadRequest.getName(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .body(fileResource);
        }
    }

    @PostMapping("/rename")
    public RedirectView renameFile(@Valid @ModelAttribute FileRenameRequest fileRenameRequest,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserEntity currentUser,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, getFirstMessage(bindingResult));
        } else {
            fileService.renameFile(currentUser.getId(), fileRenameRequest);
        }

        return new RedirectView(HOME_PAGE_URI);
    }
}

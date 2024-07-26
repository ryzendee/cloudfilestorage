package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.BreadcrumbDto;
import com.app.cloudfilestorage.dto.request.file.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.file.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.file.FileRenameRequest;
import com.app.cloudfilestorage.dto.request.file.FileUploadRequest;
import com.app.cloudfilestorage.dto.request.folder.*;
import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.service.FileService;
import com.app.cloudfilestorage.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static com.app.cloudfilestorage.utils.BreadcrumbUtil.getBreadcrumb;


@Controller
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private static final String DEFAULT_PATH = "/";
    private final FolderService folderService;
    private final FileService fileService;

    @GetMapping
    public String getMainView(@AuthenticationPrincipal UserEntity currentUser,
                              @RequestParam(defaultValue = DEFAULT_PATH) String path,
                              Model model) {
        if (!path.endsWith("/")) {
            path += "/";
        }

        List<FolderResponse> folderDtoList = folderService.getFoldersForPathByUserId(currentUser.getId(), path);
        List<FileResponse> fileDtoList = fileService.getFilesForPathByUserId(currentUser.getId(), path);
        model.addAttribute("folderList", folderDtoList);
        model.addAttribute("fileList", fileDtoList);

        //We don't need breadcrumbs at the default path
        if (!path.equals(DEFAULT_PATH)) {
            BreadcrumbDto breadcrumbDto = getBreadcrumb(path);
            model.addAttribute("breadcrumbDto", breadcrumbDto);
        }

        model.addAttribute("currentPath", path);
        model.addAllAttributes(getFolderRequestAttributeMap());
        model.addAllAttributes(getFileRequestAttributeMap());

        return "main-view";
    }

    private Map<String, Object> getFolderRequestAttributeMap() {
        return Map.of(
                "folderDeleteRequest", new FolderDeleteRequest(),
                "folderDownloadRequest", new FolderDownloadRequest(),
                "folderRenameRequest", new FolderRenameRequest(),
                "folderCreateRequest", new FolderCreateRequest(),
                "folderUploadRequest", new FolderUploadRequest()
        );
    }

    private Map<String, Object> getFileRequestAttributeMap() {
        return Map.of(
                "fileDownloadRequest", new FileDownloadRequest(),
                "fileDeleteRequest", new FileDeleteRequest(),
                "fileUploadRequest", new FileUploadRequest(),
                "fileRenameRequest", new FileRenameRequest()
        );
    }
}

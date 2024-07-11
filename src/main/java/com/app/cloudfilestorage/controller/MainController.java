package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.BreadcrumbDto;
import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.dto.request.*;
import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.service.FileService;
import com.app.cloudfilestorage.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
import java.util.Map;

import static com.app.cloudfilestorage.utils.BreadcrumbUtil.getBreadcrumb;


@Controller
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private static final String SEPARATOR = "/";
    private final FolderService folderService;
    private final FileService fileService;

    @GetMapping
    public String getMainView(@SessionAttribute UserSessionDto userSessionDto,
                              @RequestParam(defaultValue = SEPARATOR) String path,
                              Model model) {
        if (!path.endsWith(SEPARATOR)) {
            path += SEPARATOR;
        }

        List<FolderResponse> folderDtoList = folderService.getFoldersForPathByUserId(userSessionDto.id(), path);
        List<FileResponse> fileDtoList = fileService.getFilesForPathByUserId(userSessionDto.id(), path);
        model.addAttribute("folderList", folderDtoList);
        model.addAttribute("fileList", fileDtoList);

        //We don't need breadcrumbs at the default path
        if (!path.equals(SEPARATOR)) {
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
                "fileUploadRequest", new FileUploadRequest()
        );
    }
}

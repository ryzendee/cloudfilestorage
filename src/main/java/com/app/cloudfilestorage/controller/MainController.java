package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.dto.request.FileUploadRequest;
import com.app.cloudfilestorage.dto.request.FolderCreateRequest;
import com.app.cloudfilestorage.dto.request.FolderUploadRequest;
import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.service.FileService;
import com.app.cloudfilestorage.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;


@Controller("/")
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

        List<FolderResponse> folderDtoList = folderService.getFoldersForPathByUserId(userSessionDto.userId(), path);
        List<FileResponse> fileDtoList = fileService.getFilesForPathByUserId(userSessionDto.userId(), path);

        model.addAttribute("foldersList", folderDtoList);
        model.addAttribute("filesList", fileDtoList);

        model.addAttribute("folderCreateRequest", new FolderCreateRequest());
        model.addAttribute("folderUploadRequest", new FolderUploadRequest());
        model.addAttribute("fileUploadRequest", new FileUploadRequest());

        return "main-view";
    }

}

package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.dto.response.SearchResultResponse;
import com.app.cloudfilestorage.service.CloudStorageSearchService;
import com.app.cloudfilestorage.service.FileService;
import com.app.cloudfilestorage.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CloudStorageSearchServiceImpl implements CloudStorageSearchService {

    private final FolderService folderService;
    private final FileService fileService;

    @Override
    public SearchResultResponse searchByQuery(Long userId, String query) {
        List<FolderResponse> folderResponseList = searchFoldersByQuery(userId, query);
        List<FileResponse> fileResponseList = searchFilesByQuery(userId, query);

        return new SearchResultResponse(folderResponseList, fileResponseList);
    }

    private List<FolderResponse> searchFoldersByQuery(Long userId, String query) {
        return folderService.getAllFoldersByUserId(userId).stream()
                .filter(folder -> folder.path().contains(query))
                .toList();
    }

    private List<FileResponse> searchFilesByQuery(Long userId, String query) {
        return fileService.getAllFilesByUserId(userId).stream()
                .filter(file -> file.path().contains(query))
                .toList();
    }
}

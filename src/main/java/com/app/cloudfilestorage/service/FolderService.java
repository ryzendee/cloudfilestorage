package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.request.FolderCreateRequest;
import com.app.cloudfilestorage.dto.request.FolderUploadRequest;
import com.app.cloudfilestorage.dto.response.FolderResponse;

import java.util.List;

public interface FolderService {
    List<FolderResponse> getFoldersForPathByUserId(Long userId, String path);

    void createEmptyFolder(FolderCreateRequest createRequest);
    void uploadFolder(FolderUploadRequest uploadRequest);
}

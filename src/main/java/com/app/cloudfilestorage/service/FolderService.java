package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.request.FolderCreateRequest;
import com.app.cloudfilestorage.dto.request.FolderDeleteRequest;
import com.app.cloudfilestorage.dto.request.FolderDownloadRequest;
import com.app.cloudfilestorage.dto.request.FolderUploadRequest;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FolderService {
    List<FolderResponse> getFoldersForPathByUserId(Long userId, String path);

    void createEmptyFolder(Long userId, FolderCreateRequest createRequest);
    void uploadFolder(Long userId, FolderUploadRequest uploadRequest);
    void deleteFolder(Long userId, FolderDeleteRequest deleteRequest);
    Resource downloadFolder(Long userId, FolderDownloadRequest folderDownloadRequest);
}

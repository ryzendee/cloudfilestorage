package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.request.*;
import com.app.cloudfilestorage.dto.response.FolderResponse;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface FolderService {
    List<FolderResponse> getAllFoldersByUserId(Long userId);
    List<FolderResponse> getFoldersForPathByUserId(Long userId, String path);

    void createEmptyFolder(Long userId, FolderCreateRequest createRequest);
    void uploadFolder(Long userId, FolderUploadRequest uploadRequest);
    void deleteFolder(Long userId, FolderDeleteRequest deleteRequest);
    ByteArrayOutputStream downloadFolder(Long userId, FolderDownloadRequest folderDownloadRequest);
    void renameFolder(Long userId, FolderRenameRequest renameRequest);
}

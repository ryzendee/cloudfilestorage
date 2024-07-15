package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.request.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.FileRenameRequest;
import com.app.cloudfilestorage.dto.request.FileUploadRequest;
import com.app.cloudfilestorage.dto.response.FileResponse;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileService {

    List<FileResponse> getAllFilesByUserId(Long userId);
    List<FileResponse> getFilesForPathByUserId(Long userId, String path);
    void uploadFile(Long userId, FileUploadRequest fileUploadRequest);
    void deleteFile(Long userId, FileDeleteRequest fileDeleteRequest);
    Resource downloadFile(Long userId, FileDownloadRequest fileDownloadRequest);
    void renameFile(Long userId, FileRenameRequest fileRenameRequest);
}

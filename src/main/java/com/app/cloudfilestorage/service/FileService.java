package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.response.FileResponse;

import java.util.List;

public interface FileService {

    List<FileResponse> getFilesForPathByUserId(Long userId, String path);
}

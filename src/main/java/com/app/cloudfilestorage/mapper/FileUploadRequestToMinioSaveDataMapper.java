package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.FileUploadRequest;

public interface FileUploadRequestToMinioSaveDataMapper {

    MinioSaveDataDto map(String basePath, FileUploadRequest uploadRequest);
}

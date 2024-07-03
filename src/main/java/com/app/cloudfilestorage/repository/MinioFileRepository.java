package com.app.cloudfilestorage.repository;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.models.MinioObject;

import java.io.InputStream;
import java.util.List;

public interface MinioFileRepository {

    List<MinioObject> findAllFilesByPath(String path);
    void saveFile(MinioSaveDataDto minioSaveDataDto);
    void deleteFileByObjectName(String objectName);
    InputStream downloadFileByObjectName(String objectName);

}

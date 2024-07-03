package com.app.cloudfilestorage.repository;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.models.MinioObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface MinioFolderRepository {

    void createEmptyFolder(String path);
    void saveAll(List<MinioSaveDataDto> minioSaveDataDtoList);
    void deleteFolderByPath(String path);
    void renameFolder(String oldPath, String newPath);
    List<MinioObject> findAllFoldersByPath(String path);
    ByteArrayOutputStream downloadFolderByPath(String path);
}

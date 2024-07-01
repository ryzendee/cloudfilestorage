package com.app.cloudfilestorage.repository;

import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.dto.MinioSaveDataDto;

import java.io.InputStream;
import java.util.List;

public interface MinioRepository {

    void saveObject(MinioSaveDataDto minioSaveDataDto);
    void createEmptyFolder(String path);
    void saveAll(List<MinioSaveDataDto> minioSaveDataDtoList);
    void deleteAllRecursive(String path);
    void renameAllRecursive(String oldPath, String newPath);
    void moveAllRecursive(String from, String to);
    List<MinioObject> findAll(String path);
    InputStream downloadByPath(String path);
    byte[] downloadByPathAll(String path);
}

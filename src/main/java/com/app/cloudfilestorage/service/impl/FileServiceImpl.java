package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.FileUploadRequest;
import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.FileUploadRequestToMinioSaveDataMapper;
import com.app.cloudfilestorage.mapper.MinioObjectToFileResponseMapper;
import com.app.cloudfilestorage.repository.MinioRepository;
import com.app.cloudfilestorage.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.app.cloudfilestorage.utils.PathGeneratorUtil.formatPath;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioRepository minioRepository;
    private final MinioObjectToFileResponseMapper minioObjectToFileResponseMapper;
    private final FileUploadRequestToMinioSaveDataMapper minioSaveDataMapper;

    @Override
    public List<FileResponse> getFilesForPathByUserId(Long userId, String path) {
        String formattedPath = formatPath(userId, path);

        try {
            return minioRepository.findAll(formattedPath).stream()
                    .filter(minioObj -> !minioObj.isDir())
                    .map(minioObj -> minioObjectToFileResponseMapper.map(minioObj, userId))
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find files by path: {}", formattedPath, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadFile(Long userId, FileUploadRequest uploadRequest) {
        try {
            String basePath = formatPath(userId, uploadRequest.getCurrentFolderPath());
            MinioSaveDataDto minioSaveDataDto = minioSaveDataMapper.map(basePath, uploadRequest);
            minioRepository.saveObject(minioSaveDataDto);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to upload file", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteFile(Long userId, FileDeleteRequest deleteRequest) {
        try {
            String formattedPath = formatPath(userId, deleteRequest.getPath());
            minioRepository.deleteByPath(formattedPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to delete file", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Resource downloadFile(Long userId, FileDownloadRequest downloadRequest) {
        try {
            String formattedPath = formatPath(userId, downloadRequest.getPath());
            InputStream inputStream = minioRepository.downloadByPath(formattedPath);
            return new ByteArrayResource(inputStream.readAllBytes());
        } catch (MinioRepositoryException | IOException ex) {
            log.warn("Failed to download file", ex);
            throw new RuntimeException(ex);
        }
    }
}

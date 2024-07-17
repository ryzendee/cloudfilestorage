package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.FileRenameRequest;
import com.app.cloudfilestorage.dto.request.FileUploadRequest;
import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.exception.FileServiceException;
import com.app.cloudfilestorage.exception.FolderServiceException;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.FileUploadRequestToMinioSaveDataMapper;
import com.app.cloudfilestorage.mapper.MinioObjectToFileResponseMapper;
import com.app.cloudfilestorage.repository.MinioFileRepository;
import com.app.cloudfilestorage.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.app.cloudfilestorage.utils.FileNameUtil.renameFileInPath;
import static com.app.cloudfilestorage.utils.PathGeneratorUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioFileRepository minioFileRepository;
    private final MinioObjectToFileResponseMapper minioObjectToFileResponseMapper;
    private final FileUploadRequestToMinioSaveDataMapper minioSaveDataMapper;

    @Override
    public List<FileResponse> getAllFilesByUserId(Long userId) {
        try {
            String userRootFolder = formatBasePath(userId);
            return minioFileRepository.findAllFilesByPathRecursive(userRootFolder).stream()
                    .map(minioObj -> minioObjectToFileResponseMapper.map(userId, minioObj))
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find all user files", ex);
            throw new FileServiceException("Failed to find all user files");
        }
    }

    @Override
    public List<FileResponse> getFilesForPathByUserId(Long userId, String path) {
        try {
            String formattedPath = formatPathForFolder(userId, path);
            return minioFileRepository.findAllFilesByPath(formattedPath).stream()
                    .map(minioObj -> minioObjectToFileResponseMapper.map(userId, minioObj))
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find files by path: {}", path, ex);
            throw new FileServiceException("Failed to load files");
        }
    }

    @Override
    public void uploadFile(Long userId, FileUploadRequest uploadRequest) {
        try {
            String basePath = formatPathForFolder(userId, uploadRequest.getCurrentFolderPath());
            MinioSaveDataDto minioSaveDataDto = minioSaveDataMapper.map(basePath, uploadRequest);
            minioFileRepository.saveFile(minioSaveDataDto);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to upload file", ex);
            throw new FileServiceException("Failed to upload file");
        } catch (MinioObjectExistsException ex) {
            throw new FolderServiceException("This name already exists");
        }
    }

    @Override
    public void deleteFile(Long userId, FileDeleteRequest deleteRequest) {
        try {
            String formattedPath = formatPathForFile(userId, deleteRequest.getPath());
            minioFileRepository.deleteFileByObjectName(formattedPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to delete file", ex);
            throw new FileServiceException("Failed to delete file");
        }
    }

    @Override
    public Resource downloadFile(Long userId, FileDownloadRequest downloadRequest) {
        try {
            String formattedPath = formatPathForFile(userId, downloadRequest.getPath());

            try (InputStream inputStream = minioFileRepository.downloadFileByObjectName(formattedPath)) {
                return new ByteArrayResource(inputStream.readAllBytes());
            }

        } catch (MinioRepositoryException | IOException ex) {
            log.warn("Failed to download file", ex);
            throw new FileServiceException("Failed to download file");
        }
    }

    @Override
    public void renameFile(Long userId, FileRenameRequest renameRequest) {
        try {
            String pathWithUpdatedName = renameFileInPath(renameRequest.getPath(), renameRequest.getUpdatedName());

            String formattedPathWithUpdatedName = formatPathForFile(userId, pathWithUpdatedName);
            String formattedPathWithCurrentName = formatPathForFile(userId, renameRequest.getPath());

            minioFileRepository.renameFile(formattedPathWithCurrentName, formattedPathWithUpdatedName);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to rename folder", ex);
            throw new FolderServiceException("Failed to rename folder");
        } catch (MinioObjectExistsException ex) {
            throw new FolderServiceException("This name is already exists");
        }
    }
}

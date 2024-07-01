package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.*;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.exception.MappingException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.FolderUploadReqToMinioSaveDtoListMapper;
import com.app.cloudfilestorage.mapper.MinioObjectToFolderResponseMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioRepository;
import com.app.cloudfilestorage.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.app.cloudfilestorage.utils.PathGeneratorUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final MinioRepository minioRepository;
    private final MinioObjectToFolderResponseMapper minioObjectToFolderResponseMapper;
    private final FolderUploadReqToMinioSaveDtoListMapper folderUploadReqToMinioSaveDtoListMapper;
    @Override
    public List<FolderResponse> getFoldersForPathByUserId(Long userId, String path) {
        try {
            String formattedPath = formatPath(userId, path);
            return minioRepository.findAll(formattedPath).stream()
                    .filter(MinioObject::isDir)
                    .map(minioObject -> minioObjectToFolderResponseMapper.map(minioObject, userId))
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find folders for path: {}", path);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void createEmptyFolder(Long userId, FolderCreateRequest createRequest) {
        try {
            String folderPath = formatPathForFolder(userId, createRequest.getCurrentFolderPath(), createRequest.getFolderName());
            minioRepository.createEmptyFolder(folderPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to create empty folder", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadFolder(Long userId, FolderUploadRequest uploadRequest) {
        try {
            List<MinioSaveDataDto> minioSaveDataDtoList = folderUploadReqToMinioSaveDtoListMapper.map(userId, uploadRequest);
            minioRepository.saveAll(minioSaveDataDtoList);
        } catch (MappingException | MinioRepositoryException ex) {
            log.warn("Failed to upload folder", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteFolder(Long userId, FolderDeleteRequest deleteRequest) {
        try {
            String formattedPath = formatPath(userId, deleteRequest.getFolderPath());
            minioRepository.deleteAllRecursive(formattedPath);
        } catch (MinioRepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Resource downloadFolder(Long userId, FolderDownloadRequest downloadRequest) {
        try {
            String formattedPath = formatPath(userId, downloadRequest.getFolderPath());
            byte[] folder =  minioRepository.downloadByPathAll(formattedPath);
            return new ByteArrayResource(folder);
        } catch (MinioRepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void renameFolder(Long userId, FolderRenameRequest renameRequest) {
        try {
            String updatedPath = updateFolderPath(userId, renameRequest);
            String oldPath = formatPath(userId, renameRequest.getPath());
            minioRepository.renameAllRecursive(oldPath, updatedPath);
        } catch (MinioRepositoryException ex) {
            throw new MinioRepositoryException(ex);
        }
    }
}

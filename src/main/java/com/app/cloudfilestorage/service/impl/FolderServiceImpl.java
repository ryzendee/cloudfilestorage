package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.*;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.exception.FolderServiceException;
import com.app.cloudfilestorage.exception.MappingException;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.FolderUploadReqToMinioSaveDtoListMapper;
import com.app.cloudfilestorage.mapper.MinioObjectToFolderResponseMapper;
import com.app.cloudfilestorage.repository.MinioFolderRepository;
import com.app.cloudfilestorage.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.app.cloudfilestorage.utils.FolderNameUtil.renameLastFolderInPath;
import static com.app.cloudfilestorage.utils.PathGeneratorUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final MinioFolderRepository minioFolderRepository;
    private final MinioObjectToFolderResponseMapper minioObjectToFolderResponseMapper;
    private final FolderUploadReqToMinioSaveDtoListMapper folderUploadReqToMinioSaveDtoListMapper;

    @Override
    public List<FolderResponse> getAllFoldersByUserId(Long userId) {
        try {
            String userRootFolder = formatBasePath(userId);
            return minioFolderRepository.findAllFoldersByPathRecursive(userRootFolder).stream()
                    .map(minioObject -> minioObjectToFolderResponseMapper.map(minioObject, userId))
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find all user folders", ex);
            throw new FolderServiceException("Failed to find all folders");
        }
    }

    @Override
    public List<FolderResponse> getFoldersForPathByUserId(Long userId, String path) {
        try {
            String formattedPath = formatPathForFolder(userId, path);
            return minioFolderRepository.findAllFoldersByPath(formattedPath).stream()
                    .map(minioObject -> minioObjectToFolderResponseMapper.map(minioObject, userId))
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find folders for path: {}", path, ex);
            throw new FolderServiceException("Failed to load folders");
        }
    }

    @Override
    public void createEmptyFolder(Long userId, FolderCreateRequest createRequest) {
        try {
            String folderPath = formatPathForFolder(userId, createRequest.getCurrentFolderPath(), createRequest.getFolderName());
            minioFolderRepository.createEmptyFolder(folderPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to create empty folder", ex);
            throw new FolderServiceException("Failed to create empty folder");
        } catch (MinioObjectExistsException ex) {
            throw new FolderServiceException("This name is already exists");
        }
    }

    @Override
    public void uploadFolder(Long userId, FolderUploadRequest uploadRequest) {
        try {
            List<MinioSaveDataDto> minioSaveDataDtoList = folderUploadReqToMinioSaveDtoListMapper.map(userId, uploadRequest);
            minioFolderRepository.saveAll(minioSaveDataDtoList);
        } catch (MappingException | MinioRepositoryException ex) {
            log.warn("Failed to upload folder", ex);
            throw new FolderServiceException("Failed to upload folder");
        }
    }

    @Override
    public void deleteFolder(Long userId, FolderDeleteRequest deleteRequest) {
        try {
            String formattedPath = formatPathForFolder(userId, deleteRequest.getFolderPath());
            minioFolderRepository.deleteFolderByPath(formattedPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to delete folder", ex);
            throw new FolderServiceException("Failed to delete folder");
        }
    }

    @Override
    public ByteArrayOutputStream downloadFolder(Long userId, FolderDownloadRequest downloadRequest) {
        try {
            String formattedPath = formatPathForFolder(userId, downloadRequest.getFolderPath());
            return minioFolderRepository.downloadFolderByPath(formattedPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to download folder", ex);
            throw new FolderServiceException("Failed to download folder");
        }
    }

    @Override
    public void renameFolder(Long userId, FolderRenameRequest renameRequest) {
        try {
            String updatedPath = renameLastFolderInPath(renameRequest.getPath(), renameRequest.getUpdatedName());
            String formattedUpdatedPath = formatPathForFolder(userId, updatedPath);
            String formattedCurrentPath = formatPathForFolder(userId, renameRequest.getPath());

            minioFolderRepository.renameFolder(formattedCurrentPath, formattedUpdatedPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to rename folder", ex);
            throw new FolderServiceException("Failed to rename folder");
        } catch (MinioObjectExistsException ex) {
            throw new FolderServiceException("This name is already exists");
        }
    }
}

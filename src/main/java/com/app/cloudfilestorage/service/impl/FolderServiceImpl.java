package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.*;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.exception.MappingException;
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

import static com.app.cloudfilestorage.utils.PathGeneratorUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final MinioFolderRepository minioFolderRepository;
    private final MinioObjectToFolderResponseMapper minioObjectToFolderResponseMapper;
    private final FolderUploadReqToMinioSaveDtoListMapper folderUploadReqToMinioSaveDtoListMapper;

    @Override
    public List<FolderResponse> getFoldersForPathByUserId(Long userId, String path) {
        try {
            String formattedPath = formatPath(userId, path);
            return minioFolderRepository.findAllFoldersByPath(formattedPath).stream()
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
            minioFolderRepository.createEmptyFolder(folderPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to create empty folder", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadFolder(Long userId, FolderUploadRequest uploadRequest) {
        try {
            List<MinioSaveDataDto> minioSaveDataDtoList = folderUploadReqToMinioSaveDtoListMapper.map(userId, uploadRequest);
            minioFolderRepository.saveAll(minioSaveDataDtoList);
        } catch (MappingException | MinioRepositoryException ex) {
            log.warn("Failed to upload folder", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteFolder(Long userId, FolderDeleteRequest deleteRequest) {
        try {
            String formattedPath = formatPath(userId, deleteRequest.getFolderPath());
            minioFolderRepository.deleteFolderByPath(formattedPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to delete folder", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ByteArrayOutputStream downloadFolder(Long userId, FolderDownloadRequest downloadRequest) {
        try {
            String formattedPath = formatPath(userId, downloadRequest.getFolderPath());
            return minioFolderRepository.downloadFolderByPath(formattedPath);
        } catch (MinioRepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void renameFolder(Long userId, FolderRenameRequest renameRequest) {
        try {
            String updatedPath = updateFolderPath(userId, renameRequest);
            String oldPath = formatPath(userId, renameRequest.getPath());
            minioFolderRepository.renameFolder(oldPath, updatedPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to rename folder", ex);
            throw new MinioRepositoryException(ex);
        }
    }
}

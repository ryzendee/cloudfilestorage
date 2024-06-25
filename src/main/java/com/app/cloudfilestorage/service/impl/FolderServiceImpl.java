package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.request.FolderCreateRequest;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.MinioObjectToFolderResponseMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.impl.MinioRepositoryImpl;
import com.app.cloudfilestorage.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.app.cloudfilestorage.utils.PathGeneratorUtil.formatPath;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final MinioRepositoryImpl minioRepository;
    private final MinioObjectToFolderResponseMapper minioObjectToFolderResponseMapper;
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
    public void createEmptyFolder(FolderCreateRequest createRequest) {
        try {
            String folderPath = formatPath(createRequest.getOwnerId(), createRequest.getCurrentFolderPath(), createRequest.getFolderName());
            minioRepository.createEmptyFolder(folderPath);
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to create empty folder", ex);
            throw new RuntimeException(ex);
        }
    }
}

package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.MinioObjectToFolderResponseMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.impl.MinioRepositoryImpl;
import com.app.cloudfilestorage.service.FolderService;
import com.app.cloudfilestorage.utils.PathGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final MinioRepositoryImpl minioRepository;
    private final MinioObjectToFolderResponseMapper minioObjectToFolderResponseMapper;
    @Override
    public List<FolderResponse> getFoldersForPathByUserId(Long userId, String path) {
        try {
            String formattedPath = PathGeneratorUtil.formatPath(userId, path);
            return minioRepository.findAll(formattedPath).stream()
                    .filter(MinioObject::isDir)
                    .map(minioObjectToFolderResponseMapper::map)
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find folders for path: {}", path);
            throw new RuntimeException(ex);
        }
    }
}

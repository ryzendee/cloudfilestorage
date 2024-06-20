package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.MinioObjectToFileResponseMapper;
import com.app.cloudfilestorage.repository.MinioRepository;
import com.app.cloudfilestorage.service.FileService;
import com.app.cloudfilestorage.utils.PathGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioRepository minioRepository;
    private final MinioObjectToFileResponseMapper minioObjectToFileResponseMapper;

    @Override
    public List<FileResponse> getFilesForPathByUserId(Long userId, String path) {
        String formattedPath = PathGeneratorUtil.formatPath(userId, path);

        try {
            return minioRepository.findAll(formattedPath).stream()
                    .map(minioObjectToFileResponseMapper::map)
                    .toList();
        } catch (MinioRepositoryException ex) {
            log.warn("Failed to find files by path: {}", formattedPath, ex);
            throw new RuntimeException(ex);
        }
    }
}

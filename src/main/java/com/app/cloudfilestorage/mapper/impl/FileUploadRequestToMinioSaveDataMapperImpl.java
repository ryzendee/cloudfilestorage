package com.app.cloudfilestorage.mapper.impl;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.file.FileUploadRequest;
import com.app.cloudfilestorage.exception.MappingException;
import com.app.cloudfilestorage.mapper.FileUploadRequestToMinioSaveDataMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FileUploadRequestToMinioSaveDataMapperImpl implements FileUploadRequestToMinioSaveDataMapper {

    @Override
    public MinioSaveDataDto map(String basePath, FileUploadRequest uploadRequest) {
        try {
            MultipartFile file = uploadRequest.getFile();
            return new MinioSaveDataDto(
                    basePath + file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getSize()
            );

        } catch (IOException ex) {
            throw new MappingException(ex);
        }
    }
}

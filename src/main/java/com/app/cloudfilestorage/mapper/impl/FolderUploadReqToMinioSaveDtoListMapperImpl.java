package com.app.cloudfilestorage.mapper.impl;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.folder.FolderUploadRequest;
import com.app.cloudfilestorage.exception.MappingException;
import com.app.cloudfilestorage.mapper.FolderUploadReqToMinioSaveDtoListMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.app.cloudfilestorage.utils.PathUtil.formatPathForFolder;

@Component
public class FolderUploadReqToMinioSaveDtoListMapperImpl implements FolderUploadReqToMinioSaveDtoListMapper {

    @Override
    public List<MinioSaveDataDto> map(Long userId, FolderUploadRequest from) {
        try {
            String basePath = formatPathForFolder(userId, from.getCurrentFolderPath());

            List<MinioSaveDataDto> minioSaveDataDtoList = new ArrayList<>();
            for (MultipartFile file : from.getFiles()) {
                MinioSaveDataDto dto = new MinioSaveDataDto(
                        basePath + file.getOriginalFilename(),
                        file.getInputStream(),
                        file.getSize()
                );
                minioSaveDataDtoList.add(dto);
            }

            return minioSaveDataDtoList;
        } catch (IOException ex) {
            throw new MappingException(ex);
        }
    }
}

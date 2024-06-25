package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.FolderUploadRequest;

import java.util.List;

public interface FolderUploadReqToMinioSaveDtoListMapper extends BaseMapper<FolderUploadRequest, List<MinioSaveDataDto>> {

}

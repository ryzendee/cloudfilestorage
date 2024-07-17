package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FolderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFolderResponseMapper extends BaseMinioObjectMapper<FolderResponse>{

}

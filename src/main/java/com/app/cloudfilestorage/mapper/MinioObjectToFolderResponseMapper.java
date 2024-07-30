package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.models.MinioObject;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFolderResponseMapper extends BaseMinioObjectMapper<FolderResponse> {

    @Mapping(source = "path", target = "name", qualifiedByName = "formatNameFromPath")
    @Mapping(source = "path", target = "path", qualifiedByName = "removeRootUserFolderFromPath")
    FolderResponse map(@Context Long userId, MinioObject from);

}

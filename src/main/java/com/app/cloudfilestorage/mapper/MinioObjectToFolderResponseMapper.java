package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.utils.FileNameFormatterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFolderResponseMapper extends BaseMapper<MinioObject, FolderResponse> {
    @Mapping(source = "path", target = "name", qualifiedByName = "formatFolderNameFromPath")
    FolderResponse map(MinioObject from);

    @Named("formatFolderNameFromPath")
    default String formatFolderNameFromPath(String path) {
        return FileNameFormatterUtil.formatFilenameFromPath(path);
    }
}

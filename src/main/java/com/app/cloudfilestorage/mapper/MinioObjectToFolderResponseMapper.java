package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.utils.FileNameFormatterUtil;
import com.app.cloudfilestorage.utils.PathGeneratorUtil;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFolderResponseMapper {
    @Mapping(source = "path", target = "name", qualifiedByName = "formatFolderNameFromPath")
    @Mapping(source = "path", target = "path", qualifiedByName = "removeRootUserFolderFromPath")
    FolderResponse map(MinioObject from, @Context Long userId);

    @Named("formatFolderNameFromPath")
    default String formatFolderNameFromPath(String path) {
        return FileNameFormatterUtil.formatFilenameFromPath(path);
    }

    @Named("removeRootUserFolderFromPath")
    default String removeRootUserFolderFromPath(@Context Long userId, String path) {
        return PathGeneratorUtil.removeTemplateFromPath(userId, path);
    }
}

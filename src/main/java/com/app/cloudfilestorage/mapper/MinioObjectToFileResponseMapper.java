package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.utils.FileNameFormatterUtil;
import com.app.cloudfilestorage.utils.PathGeneratorUtil;
import org.mapstruct.*;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFileResponseMapper {

    @Mapping(source = "path", target = "name", qualifiedByName = "formatFilenameFromPath")
    @Mapping(source = "path", target = "path", qualifiedByName = "removeRootUserFolderFromPath")
    @Mapping(source = "size", target = "formattedSize", qualifiedByName = "formatSize")
    FileResponse map(MinioObject from, @Context Long userId);

    @Named("formatFilenameFromPath")
    default String formatFilenameFromPath(String path) {
        return FileNameFormatterUtil.formatFilenameFromPath(path);
    }

    @Named("removeRootUserFolderFromPath")
    default String removeRootUserFolderFromPath(@Context Long userId, String path) {
        return PathGeneratorUtil.removeTemplateFromPath(userId, path);
    }

    @Named("formatSize")
    default String formatSize(long size) {
        return byteCountToDisplaySize(size);
    }
}

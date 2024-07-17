package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.models.MinioObject;
import org.mapstruct.Context;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.nio.file.Paths;

import static com.app.cloudfilestorage.utils.PathGeneratorUtil.removeTemplateFromPath;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

public interface BaseMinioObjectMapper <T> {

    @Mapping(source = "path", target = "name", qualifiedByName = "formatFolderNameFromPath")
    @Mapping(source = "path", target = "path", qualifiedByName = "removeRootUserFolderFromPath")
    @Mapping(source = "size", target = "formattedSize", qualifiedByName = "formatSize")
    T map(@Context Long userId, MinioObject minioObject);

    @Named("formatFolderNameFromPath")
    default String formatNameFromPath(String path) {
        return Paths.get(path)
                .getFileName()
                .toString();
    }

    @Named("removeRootUserFolderFromPath")
    default String removeRootUserFolderFromPath(@Context Long userId, String path) {
        return removeTemplateFromPath(userId, path);
    }

    @Named("formatSize")
    default String formatSize(long size) {
        return byteCountToDisplaySize(size);
    }
}

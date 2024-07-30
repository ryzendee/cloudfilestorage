package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.models.MinioObject;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.nio.file.Paths;

import static com.app.cloudfilestorage.utils.PathUtil.removeTemplateFromPath;

public interface BaseMinioObjectMapper <T> {

    T map(@Context Long userId, MinioObject from);

    @Named("formatNameFromPath")
    default String formatNameFromPath(String path) {
        return Paths.get(path)
                .getFileName()
                .toString();
    }

    @Named("removeRootUserFolderFromPath")
    default String removeRootUserFolderFromPath(@Context Long userId, String path) {
        return removeTemplateFromPath(userId, path);
    }

}

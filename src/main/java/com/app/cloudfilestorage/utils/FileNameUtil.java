package com.app.cloudfilestorage.utils;

import lombok.experimental.UtilityClass;

import java.nio.file.Paths;

@UtilityClass
public class FileNameUtil {

    private static final String SEPARATOR = ".";

    public static String renameFileInPath(String path, String updatedName) {
        String currentName = Paths.get(path)
                .getFileName()
                .toString();

        String extension = getExtensionFromFileName(currentName);
        String updatedNameWithExtension = extension.isBlank()
                ? updatedName
                : updatedName + SEPARATOR + extension;

        int currentNameIdx = path.lastIndexOf(currentName);

        return path.substring(0, currentNameIdx) + updatedNameWithExtension;
    }

    private static String getExtensionFromFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf(SEPARATOR);

        return (dotIndex == -1)
                ? ""
                : fileName.substring(dotIndex + 1);
    }
}

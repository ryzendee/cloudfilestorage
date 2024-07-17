package com.app.cloudfilestorage.utils;

import lombok.experimental.UtilityClass;

import java.nio.file.Paths;

@UtilityClass
public class FolderNameUtil {
    private static final String SEPARATOR = "/";
    public static String renameLastFolderInPath(String path, String updatedName) {
        String currentName = Paths.get(path)
                .getFileName()
                .toString();

        int currentNameIdx = path.lastIndexOf(currentName);

        return path.substring(0, currentNameIdx) + updatedName + SEPARATOR;
    }
}

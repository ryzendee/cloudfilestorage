package com.app.cloudfilestorage.utils;

import lombok.experimental.UtilityClass;

import java.nio.file.Paths;

@UtilityClass
public class FileNameFormatterUtil {

    public static String formatFilenameFromPath(String path) {
        return Paths.get(path)
                .getFileName()
                .toString();
    }
}

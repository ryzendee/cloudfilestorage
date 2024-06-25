package com.app.cloudfilestorage.utils;

public final class PathGeneratorUtil {
    private static final String DEFAULT_PATH = "/";
    private static final String TEMPLATE = "user-%d-files/";

    public static String formatPath(Long userId, String path) {
        if (path.equals(DEFAULT_PATH) || path.isEmpty()) {
            return TEMPLATE.formatted(userId);
        }

        if (path.startsWith(TEMPLATE.formatted(userId))) {
            return path;
        }

        return TEMPLATE.formatted(userId) + path;
    }
    
    public static String formatPathForFolder(Long userId, String currentFolderPath, String path) {
        if (!path.endsWith("/")) {
            path += "/";
        }

        if (currentFolderPath.equals("/")) {
            return TEMPLATE.formatted(userId) + path;
        }

        return TEMPLATE.formatted(userId) + currentFolderPath + path;
    }

    public static String removeTemplateFromPath(Long userId, String path) {
        String pathToRemove = TEMPLATE.formatted(userId);
        return path.replace(pathToRemove, "");
    }
}

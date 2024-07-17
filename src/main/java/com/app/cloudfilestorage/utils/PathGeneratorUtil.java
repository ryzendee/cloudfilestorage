package com.app.cloudfilestorage.utils;

public final class PathGeneratorUtil {
    private static final String SEPARATOR = "/";
    private static final String TEMPLATE = "user-%d-files/";

    public static String formatBasePath(Long userId) {
        return formatPathWithTemplate(userId);
    }
    
    public static String formatPathForFolder(Long userId, String currentFolderPath, String folderPath) {
        if (currentFolderPath.equals(SEPARATOR)) {
            return formatPathWithTemplate(userId) + folderPath;
        }

        folderPath = folderPath.endsWith(SEPARATOR)
                ? folderPath
                : folderPath + SEPARATOR;

        return formatPathWithTemplate(userId) + currentFolderPath + folderPath;
    }

    public static String formatPathForFolder(Long userId, String folderPath) {
        if (folderPath.equals(SEPARATOR)) {
            return formatPathWithTemplate(userId);
        }

        folderPath = folderPath.endsWith(SEPARATOR)
                ? folderPath
                : folderPath + SEPARATOR;

        return formatPathWithTemplate(userId) + SEPARATOR + folderPath;
    }

    public static String formatPathForFile(Long userId, String filePath) {
        if (filePath.startsWith(SEPARATOR)) {
            return formatPathWithTemplate(userId) + filePath.substring(1);
        }

        return formatPathWithTemplate(userId) + filePath;
    }

    public static String removeTemplateFromPath(Long userId, String path) {
        String pathToRemove = formatPathWithTemplate(userId);
        return path.replace(pathToRemove, "");
    }

    private static String formatPathWithTemplate(Long userId) {
        return TEMPLATE.formatted(userId);
    }
}

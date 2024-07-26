package com.app.cloudfilestorage.utils;

public final class PathUtil {
    private static final String SEPARATOR = "/";
    private static final String TEMPLATE = "user-%d-files";
    private static final String REGEX = "/{2,}";


    public static String formatBasePath(Long userId) {
        return formatTemplate(userId) + SEPARATOR;
    }

    public static String formatPathForFolder(Long userId, String currentFolderPath, String folderPath) {
        String basePath = formatTemplate(userId);
        String joinedPath = String.join(SEPARATOR, basePath, currentFolderPath, folderPath)
                .replaceAll(REGEX, SEPARATOR);

        return joinedPath.endsWith(SEPARATOR)
                ? joinedPath
                : joinedPath + SEPARATOR;
    }

    public static String formatPathForFolder(Long userId, String folderPath) {
        String basePath = formatTemplate(userId);
        String joinedPath = String.join(SEPARATOR, basePath, folderPath)
                .replaceAll(REGEX, SEPARATOR);

        return joinedPath.endsWith(SEPARATOR)
                ? joinedPath
                : joinedPath + SEPARATOR;
    }

    public static String formatPathForFile(Long userId, String filePath) {
        String basePath = formatTemplate(userId);

        return String.join(SEPARATOR, basePath, filePath)
                .replaceAll(REGEX, SEPARATOR);
    }

    public static String removeTemplateFromPath(Long userId, String path) {
        String pathToRemove = formatTemplate(userId);

        return path.replace(pathToRemove, "");
    }

    private static String formatTemplate(Long userId) {
        return TEMPLATE.formatted(userId);
    }
}

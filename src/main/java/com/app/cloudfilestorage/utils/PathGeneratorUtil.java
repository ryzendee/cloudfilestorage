package com.app.cloudfilestorage.utils;

import com.app.cloudfilestorage.dto.request.FolderRenameRequest;

public final class PathGeneratorUtil {
    private static final String DEFAULT_PATH = "/";
    private static final String TEMPLATE = "user-%d-files/";

    public static String formatPath(Long userId, String path) {
        if (path.equals(DEFAULT_PATH) || path.isEmpty()) {
            return formatPathWithTemplate(userId);
        }

        if (path.startsWith(TEMPLATE.formatted(userId))) {
            return path;
        }

        return formatPathWithTemplate(userId) + path;
    }

    public static String formatPath(Long userId) {
        return formatPathWithTemplate(userId);
    }
    
    public static String formatPathForFolder(Long userId, String currentFolderPath, String path) {
        if (!path.endsWith("/")) {
            path += "/";
        }

        if (currentFolderPath.equals("/")) {
            return formatPathWithTemplate(userId) + path;
        }

        return formatPathWithTemplate(userId) + currentFolderPath + path;
    }

    public static String removeTemplateFromPath(Long userId, String path) {
        String pathToRemove = formatPathWithTemplate(userId);
        return path.replace(pathToRemove, "");
    }

    public static String updateFolderPath(Long userId, FolderRenameRequest renameRequest) {
        String updatedPath = updateNameInPath(renameRequest);
        return formatPath(userId, updatedPath);
    }

    private static String updateNameInPath(FolderRenameRequest renameRequest) {
        return renameRequest.getPath()
                .replaceFirst(renameRequest.getCurrentName(), renameRequest.getUpdatedName());
    }

    private static String formatPathWithTemplate(Long userId) {
        return TEMPLATE.formatted(userId);
    }
}

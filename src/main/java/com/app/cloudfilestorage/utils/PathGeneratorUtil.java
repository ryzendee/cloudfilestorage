package com.app.cloudfilestorage.utils;

public final class PathGeneratorUtil {
    private static final String DEFAULT_PATH = "/";
    private static final String TEMPLATE = "user-%d-files/";

    public static String formatPath(Long userId, String path) {
        if (path.equals(DEFAULT_PATH)) {
            return TEMPLATE.formatted(userId);
        }

        return TEMPLATE.formatted(userId) + path;
    }
}

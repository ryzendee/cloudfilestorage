package com.app.cloudfilestorage.utils;

import com.app.cloudfilestorage.dto.BreadcrumbDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreadcrumbUtil {
    private static final String SEPARATOR = "/";

    public static BreadcrumbDto getBreadcrumb(String path) {
        List<String> folderNamesList = getFoldersNamesFromPath(path);
        List<String> folderPathsList = getFolderPathsFromPath(path);

        return new BreadcrumbDto(folderNamesList, folderPathsList);
    }

    private static List<String> getFoldersNamesFromPath(String path) {
        return Arrays.asList(path.split(SEPARATOR));
    }

    private static List<String> getFolderPathsFromPath(String path) {
        List<String> folderPathsList = new ArrayList<>();
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                folderPathsList.add(path.substring(0, i));
            }
        }

        return folderPathsList;
    }
}

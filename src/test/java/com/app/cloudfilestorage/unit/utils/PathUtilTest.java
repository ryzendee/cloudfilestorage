package com.app.cloudfilestorage.unit.utils;

import com.app.cloudfilestorage.utils.PathUtil;
import org.junit.jupiter.api.Test;

import static com.app.cloudfilestorage.utils.PathUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class PathUtilTest {

    private static final String FORMATTED_TEMPLATE = "user-1-files";
    private static final Long USER_ID = 1L;

    @Test
    void removeTemplateFromPath_pathContainsTemplate_returnsPathWithoutTemplate() {
        String path = "user-1-files/folder/subfolder/";
        String expected = "/folder/subfolder/";

        String pathWithoutTemplate = PathUtil.removeTemplateFromPath(USER_ID, path);
        assertThat(pathWithoutTemplate).isEqualTo(expected);
    }

    @Test
    void formatBasePath_withUserId_returnsFormattedPath() {
        String formattedPath = formatBasePath(USER_ID);
        assertThat(formattedPath).isEqualTo(FORMATTED_TEMPLATE + "/");
    }

    @Test
    void formatPathForFolder_currentFolderPathEqualsSeparator_returnsFormattedPath() {
        String currentFolderPath = "/";
        String folderPath = "/test-folder";
        String expectedPath = FORMATTED_TEMPLATE + "/test-folder/";

        String formattedPath = formatPathForFolder(USER_ID, currentFolderPath, folderPath);
        assertThat(formattedPath).isEqualTo(expectedPath);
    }

    @Test
    void formatPathForFolder_currentFolderPathStartsWithSeparator_returnsFormattedPath() {
        String currentFolderPath = "/current-folder";
        String folderPath = "/test-folder";
        String expectedPath = FORMATTED_TEMPLATE + "/current-folder/test-folder/";

        String formattedPath = formatPathForFolder(USER_ID, currentFolderPath, folderPath);
        assertThat(formattedPath).isEqualTo(expectedPath);
    }

    @Test
    void formatPathForFolder_withCurrentFolder_returnsFormattedPath() {
        String currentFolderPath = "current-folder";
        String folderPath = "/test-folder";
        String expectedPath = FORMATTED_TEMPLATE + "/current-folder/test-folder/";

        String formattedPath = formatPathForFolder(USER_ID, currentFolderPath, folderPath);
        assertThat(formattedPath).isEqualTo(expectedPath);
    }
    @Test
    void formatPathForFolder_folderPathWithoutSeparatorAtTheEnd_returnsFormattedPath() {
        String currentFolderPath = "/current-folder";
        String folderPath = "/test-folder";
        String expectedPath = FORMATTED_TEMPLATE + "/current-folder/test-folder/";

        String formattedPath = formatPathForFolder(USER_ID, currentFolderPath, folderPath);
        assertThat(formattedPath).isEqualTo(expectedPath);
    }

    @Test
    void formatPathForFolder_withFolderPath_returnsFormattedPath() {
        String folderPath = "test-folder";
        String expectedPath = FORMATTED_TEMPLATE + "/test-folder/";

        String formattedPath = formatPathForFolder(USER_ID, folderPath);
        assertThat(formattedPath).isEqualTo(expectedPath);
    }

    @Test
    void formatPathForFile_filePathDoNotStartsWithSeparator_returnsFormattedPath() {
        String filePath = "folder/file.txt";
        String expectedPath = FORMATTED_TEMPLATE + "/folder/file.txt";

        String formattedPath = formatPathForFile(USER_ID, filePath);
        assertThat(formattedPath).isEqualTo(expectedPath);

    }

    @Test
    void formatPathForFile_filePathStartsWithSeparator_returnsFormattedPath() {
        String filePath = "/folder/file.txt";
        String expectedPath = FORMATTED_TEMPLATE + "/folder/file.txt";

        String formattedPath = formatPathForFile(USER_ID, filePath);
        assertThat(formattedPath).isEqualTo(expectedPath);
    }

}

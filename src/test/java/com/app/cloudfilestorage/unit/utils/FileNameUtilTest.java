package com.app.cloudfilestorage.unit.utils;

import org.junit.jupiter.api.Test;

import static com.app.cloudfilestorage.utils.FileNameUtil.renameFileInPath;
import static org.assertj.core.api.Assertions.assertThat;

public class FileNameUtilTest {

    @Test
    void renameFileInPath_fileWithExtension_returnsRenamedPath() {
        String currentPath = "/folder/subfolder/text.txt";
        String updatedName = "updated";
        String expectedPath = "/folder/subfolder/updated.txt";

        String renamedPath = renameFileInPath(currentPath, updatedName);
        assertThat(renamedPath).isEqualTo(expectedPath);
    }

    @Test
    void renameFileInPath_fileWithoutExtension_returnsRenamedPath() {
        String currentPath = "/folder/subfolder/text";
        String updatedName = "updated";
        String expectedPath = "/folder/subfolder/updated";

        String renamedPath = renameFileInPath(currentPath, updatedName);
        assertThat(renamedPath).isEqualTo(expectedPath);
    }
}

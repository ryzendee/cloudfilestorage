package com.app.cloudfilestorage.unit.utils;

import org.junit.jupiter.api.Test;

import static com.app.cloudfilestorage.utils.FolderNameUtil.renameLastFolderInPath;
import static org.assertj.core.api.Assertions.assertThat;

public class FolderNameUtilTest {


    @Test
    void renameLastFolderInPath_pathEndsWithSeparator_returnsRenamedPath() {
        String currentFolderPath = "/folder/subfolder";
        String updatedName = "renamed-subfolder";
        String expectedPath = "/folder/renamed-subfolder";

        String renamedPath = renameLastFolderInPath(currentFolderPath, updatedName);
        assertThat(renamedPath).isEqualTo(expectedPath);
    }
}

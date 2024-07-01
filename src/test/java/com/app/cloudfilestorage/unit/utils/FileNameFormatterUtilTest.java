package com.app.cloudfilestorage.unit.utils;

import com.app.cloudfilestorage.utils.FileNameFormatterUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileNameFormatterUtilTest {

    @Test
    void formatFilenameFromPath_r_r() {
        String path = "folder/subfolder/file.txt";

        String fileName = FileNameFormatterUtil.formatFilenameFromPath(path);
        assertThat(fileName).isEqualTo("file.txt");
    }

    @Test
    void formatFileNameFromPath_withSeparatorAtTheEnd_returnsFilename() {
        String path = "folder/subfolder/file.txt";

        String fileName = FileNameFormatterUtil.formatFilenameFromPath(path);
        assertThat(fileName).isEqualTo("file.txt");
    }
}

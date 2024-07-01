package com.app.cloudfilestorage.unit.utils;

import com.app.cloudfilestorage.dto.request.FolderRenameRequest;
import com.app.cloudfilestorage.utils.PathGeneratorUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PathGeneratorUtilTest {

    private static final Long USER_ID = 1L;

    @Test
    void removeTemplateFromPath_pathContainsTemplate_returnsPathWithoutTemplate() {
        String path = "user-1-files/folder/subfolder/";

        String pathWithoutTemplate = PathGeneratorUtil.removeTemplateFromPath(USER_ID, path);
        assertThat(pathWithoutTemplate).isEqualTo("folder/subfolder/");
    }

    @Test
    void updateFolderPath_t_replacesOldNameWithUpdatedNameInPath() {
        FolderRenameRequest renameRequest = new FolderRenameRequest("updated", "current", "current/subfolder");

        String updatedPath = PathGeneratorUtil.updateFolderPath(USER_ID, renameRequest);
        assertThat(updatedPath).isEqualTo("updated/subfolder");
    }
}

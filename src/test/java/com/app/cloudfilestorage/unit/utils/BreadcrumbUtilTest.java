package com.app.cloudfilestorage.unit.utils;

import com.app.cloudfilestorage.dto.BreadcrumbDto;
import com.app.cloudfilestorage.utils.BreadcrumbUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BreadcrumbUtilTest {

    @Test
    void getBreadcrumb_nonEmptyPath_returnsDtoWithFilledLists() {
        String path = "home/user/documents";

        BreadcrumbDto breadcrumbDto = BreadcrumbUtil.getBreadcrumb(path);

        assertThat(breadcrumbDto.getFolderNamesList()).containsExactly("home", "user", "documents");
        assertThat(breadcrumbDto.getFolderPathsList()).containsExactly("home", "home/user");
    }

    @Test
    void getBreadcrumb_shortPath_returnsDtoWithEmptyLists() {
        String path = "/";

        BreadcrumbDto breadcrumbDto = BreadcrumbUtil.getBreadcrumb(path);

        assertThat(breadcrumbDto.getFolderNamesList()).isEmpty();
        assertThat(breadcrumbDto.getFolderPathsList()).containsExactly("");
    }

    @Test
    void getBreadcrumb_emptyPath_returnsDtoWithEmptyLists() {
        String path = " ";

        BreadcrumbDto breadcrumbDto = BreadcrumbUtil.getBreadcrumb(path);

        assertThat(breadcrumbDto.getFolderNamesList()).containsExactly(path);
        assertThat(breadcrumbDto.getFolderPathsList()).isEmpty();
    }
}

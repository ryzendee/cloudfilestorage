package com.app.cloudfilestorage.unit.controller;

import com.app.cloudfilestorage.config.TestSecurityConfig;
import com.app.cloudfilestorage.controller.MainController;
import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.service.FileService;
import com.app.cloudfilestorage.service.FolderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MainController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class MainControllerTest {

    private static final String BASE_PATH = "/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;
    @MockBean
    private FolderService folderService;

    @DisplayName("Get main view: should return view with file and folder lists attributes")
    @Test
    void getMainView_withBasePath_shouldReturnView() throws Exception {
        var folders = getListOfFolderResponse();
        var files = getListOfFileResponse();
        UserEntity userEntity = getUserEntity();

        when(folderService.getFoldersForPathByUserId(userEntity.getId(), BASE_PATH))
                .thenReturn(folders);
        when(fileService.getFilesForPathByUserId(userEntity.getId(), BASE_PATH))
                .thenReturn(files);

        mockMvc.perform(
                get(BASE_PATH)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                view().name("main-view"),
                model().attributeExists("folderList", "fileList")
        );

        verify(folderService).getFoldersForPathByUserId(userEntity.getId(), BASE_PATH);
        verify(fileService).getFilesForPathByUserId(userEntity.getId(), BASE_PATH);
    }

    @DisplayName("Get main view: should return view with breadcrumb attribute")
    @Test
    void getMainView_withExtendedPath_shouldReturnView() throws Exception {
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                get(BASE_PATH)
                        .param("path", "/myfiles/texts")
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                view().name("main-view"),
                model().attributeExists("breadcrumbDto")
        );
    }

    private List<FolderResponse> getListOfFolderResponse() {
        FolderResponse folderResponse = new FolderResponse("folder", "/folder", "15 MB");
        return List.of(folderResponse);
    }

    private List<FileResponse> getListOfFileResponse() {
        FileResponse fileResponse = new FileResponse("name", "/file", "txt", "15 KB", "2022-02-02");
        return List.of(fileResponse);
    }

    private UserEntity getUserEntity() {
        return new UserEntity(1L, "test-username", "test-password");
    }
}

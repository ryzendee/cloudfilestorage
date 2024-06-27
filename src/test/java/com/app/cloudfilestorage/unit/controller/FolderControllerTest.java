package com.app.cloudfilestorage.unit.controller;

import com.app.cloudfilestorage.config.TestSecurityConfig;
import com.app.cloudfilestorage.controller.FolderController;
import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.dto.request.FolderCreateRequest;
import com.app.cloudfilestorage.dto.request.FolderDeleteRequest;
import com.app.cloudfilestorage.dto.request.FolderDownloadRequest;
import com.app.cloudfilestorage.dto.request.FolderUploadRequest;
import com.app.cloudfilestorage.service.FolderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(FolderController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class FolderControllerTest {

    private static final String HOME_PAGE = "/";
    private static final String BASE_PATH = "/folders";
    private static final String SESSION_ATR_USER = "userSessionDto";
    private static final String FLASH_ATR_SUCCESS_MESSAGE = "successMessage";
    private static final String FLASH_ATR_VALIDATION_ERROR_MESSAGE = "validationErrorMessage";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FolderService folderService;

    static Stream<Arguments> getInvalidArgsForFolderName() {
        return Stream.of(
                arguments(named("Folder name is blank", "  ")),
                arguments(named("Folder name is null", null))
        );
    }
    static Stream<Arguments> getInvalidArgsForFolderPath() {
        return Stream.of(
                arguments(named("Current folder path is blank", "  ")),
                arguments(named("Current folder path is null", null))
        );
    }

    @DisplayName("Upload folder: success")
    @Test
    void uploadFolder_validRequest_uploadsAndRedirectsToHomeWithSuccessMessage() throws Exception {
        List<MultipartFile> mockFileList = getListWithMockFile();
        FolderUploadRequest uploadRequest = new FolderUploadRequest("current-path", mockFileList);
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("folderUploadRequest", uploadRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(folderService).uploadFolder(user.id(), uploadRequest);
    }

    @DisplayName("Upload folder: validation failed (invalid currentFolderPath)")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void uploadFolder_invalidCurrentFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidCurrentFolderPath) throws Exception {
        List<MultipartFile> mockFileList = getListWithMockFile();
        FolderUploadRequest uploadRequest = new FolderUploadRequest(invalidCurrentFolderPath, mockFileList);
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("folderUploadRequest", uploadRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).uploadFolder(user.id(), uploadRequest);
    }

    @DisplayName("Upload folder: validation failed (empty fileList)")
    @Test
    void uploadFolder_empty_redirectsToHomeWithValidationErrorMessage() throws Exception {
        List<MultipartFile> mockFileList = new ArrayList<>();
        FolderUploadRequest uploadRequest = new FolderUploadRequest("folder-path", mockFileList);
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("folderUploadRequest", uploadRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).uploadFolder(user.id(), uploadRequest);
    }

    @DisplayName("Create empty folder: success")
    @Test
    void createEmptyFolder_validRequest_createsAndRedirectsToHomeWithSuccessMessage() throws Exception {
        FolderCreateRequest createRequest = new FolderCreateRequest("current-path", "name");
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/empty")
                        .flashAttr("folderCreateRequest", createRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(folderService).createEmptyFolder(user.id(), createRequest);
    }

    @DisplayName("Create empty folder: validation failed (invalid name)")
    @MethodSource("getInvalidArgsForFolderName")
    @ParameterizedTest
    void createEmptyFolder_invalidFolderName_redirectsToHomeWithValidationErrorMessage(String invalidName) throws Exception {
        FolderCreateRequest invalidCreateRequest = new FolderCreateRequest("current-path", invalidName);
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/empty")
                        .flashAttr("folderCreateRequest", invalidCreateRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).createEmptyFolder(user.id(), invalidCreateRequest);
    }

    @DisplayName("CreateEmptyFolder: validation failed (invalid currentFolderPath)")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void createEmptyFolder_invalidCurrentFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidCurrentFolderPath) throws Exception {
        FolderCreateRequest invalidCreateRequest = new FolderCreateRequest(invalidCurrentFolderPath, "folder-name");
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/empty")
                        .flashAttr("folderCreateRequest", invalidCreateRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).createEmptyFolder(user.id(), invalidCreateRequest);
    }


    @DisplayName("Delete folder: success")
    @Test
    void deleteFolder_validRequest_deletesAndRedirectsToHomeWithSuccessMessage() throws Exception {
        FolderDeleteRequest deleteRequest = new FolderDeleteRequest("test-path");
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/delete")
                        .flashAttr("folderDeleteRequest", deleteRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(folderService).deleteFolder(user.id(), deleteRequest);
    }

    @DisplayName("Delete folder: validation failed (invalid folderPath)")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void deleteFolder_invalidFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidFolderPath) throws Exception {
        FolderDeleteRequest deleteRequest = new FolderDeleteRequest(invalidFolderPath);
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/delete")
                        .flashAttr("folderDeleteRequest", deleteRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).deleteFolder(user.id(), deleteRequest);
    }


    @DisplayName("Download folder: success")
    @Test
    void downloadFolder_validRequest_downloadsWithExpectedHeaderAndContentAndRedirectsToHome() throws Exception {
        FolderDownloadRequest downloadRequest = new FolderDownloadRequest("name", "path");
        UserSessionDto user = getUserSessionDto();
        String expectedHeaderValue = "attachment; filename=name.zip";
        byte[] expectedContent = "test".getBytes();
        Resource mockResource = new ByteArrayResource(expectedContent);

        when(folderService.downloadFolder(user.id(), downloadRequest))
                .thenReturn(mockResource);

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("folderDownloadRequest", downloadRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().isOk(),
                header().string(HttpHeaders.CONTENT_DISPOSITION, expectedHeaderValue),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM),
                content().bytes(expectedContent)
        );

        verify(folderService).downloadFolder(user.id(), downloadRequest);
    }


    @DisplayName("Download folder: validation failed (invalid folderName)")
    @MethodSource("getInvalidArgsForFolderName")
    @ParameterizedTest
    void downloadFolder_invalidFolderName_redirectsToHomeWithValidationErrorMessage(String invalidName) throws Exception {
        FolderDownloadRequest folderDownloadRequest = new FolderDownloadRequest(invalidName, "path");
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("folderDownloadRequest", folderDownloadRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE)
        );

        verify(folderService, never()).downloadFolder(user.id(), folderDownloadRequest);
    }

    @DisplayName("Download folder: validation failed (invalid folderPath)")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void downloadFolder_invalidFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidPath) throws Exception {
        FolderDownloadRequest folderDownloadRequest = new FolderDownloadRequest("name", invalidPath);
        UserSessionDto user = getUserSessionDto();

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("folderDownloadRequest", folderDownloadRequest)
                        .with(csrf())
                        .sessionAttr(SESSION_ATR_USER, user)
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE)
        );

        verify(folderService, never()).downloadFolder(user.id(), folderDownloadRequest);
    }


    private UserSessionDto getUserSessionDto() {
        return new UserSessionDto(1L);
    }

    private List<MultipartFile> getListWithMockFile() {
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);
        return List.of(mockFile);
    }
}

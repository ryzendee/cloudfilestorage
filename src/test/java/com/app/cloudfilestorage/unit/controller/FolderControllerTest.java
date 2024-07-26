package com.app.cloudfilestorage.unit.controller;

import com.app.cloudfilestorage.config.TestSecurityConfig;
import com.app.cloudfilestorage.controller.FolderController;
import com.app.cloudfilestorage.dto.request.folder.FolderCreateRequest;
import com.app.cloudfilestorage.dto.request.folder.FolderDeleteRequest;
import com.app.cloudfilestorage.dto.request.folder.FolderDownloadRequest;
import com.app.cloudfilestorage.dto.request.folder.FolderUploadRequest;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.enums.FlashAttr;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FolderController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class FolderControllerTest {

    private static final String HOME_PAGE = "/";
    private static final String BASE_PATH = "/folders";
    private static final String FLASH_ATR_VALIDATION_ERROR_MESSAGE = FlashAttr.VALIDATION_ERROR_MESSAGE.getName();
    private static final String FLASH_ATR_SUCCESS_MESSAGE = FlashAttr.SUCCESS_MESSAGE.getName();
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FolderService folderService;

    @DisplayName("Upload folder: should upload and redirect to home with successMessage flash attribute")
    @Test
    void uploadFolder_validRequest_uploadsAndRedirectsToHomeWithSuccessMessage() throws Exception {
        List<MultipartFile> mockFileList = getListWithMockFile();
        FolderUploadRequest uploadRequest = new FolderUploadRequest("current-path", mockFileList);
        UserEntity userEntity  = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("folderUploadRequest", uploadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(folderService).uploadFolder(userEntity.getId(), uploadRequest);
    }

    @DisplayName("Upload folder: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void uploadFolder_invalidCurrentFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidCurrentFolderPath) throws Exception {
        List<MultipartFile> mockFileList = getListWithMockFile();
        FolderUploadRequest uploadRequest = new FolderUploadRequest(invalidCurrentFolderPath, mockFileList);
        UserEntity userEntity  = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("folderUploadRequest", uploadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).uploadFolder(userEntity.getId(), uploadRequest);
    }

    @DisplayName("Upload folder: should redirect to home with validationErrorMessage flash attribute")
    @Test
    void uploadFolder_empty_redirectsToHomeWithValidationErrorMessage() throws Exception {
        List<MultipartFile> mockFileList = new ArrayList<>();
        FolderUploadRequest uploadRequest = new FolderUploadRequest("folder-path", mockFileList);
        UserEntity userEntity  = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("folderUploadRequest", uploadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).uploadFolder(userEntity.getId(), uploadRequest);
    }

    @DisplayName("Create empty folder: should create folder and redirect to home with successMessage flash attribute")
    @Test
    void createEmptyFolder_validRequest_createsAndRedirectsToHomeWithSuccessMessage() throws Exception {
        FolderCreateRequest createRequest = new FolderCreateRequest("current-path", "name");
        UserEntity userEntity  = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/empty")
                        .flashAttr("folderCreateRequest", createRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(folderService).createEmptyFolder(userEntity.getId(), createRequest);
    }

    @DisplayName("Create empty folder: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderName")
    @ParameterizedTest
    void createEmptyFolder_invalidFolderName_redirectsToHomeWithValidationErrorMessage(String invalidName) throws Exception {
        FolderCreateRequest invalidCreateRequest = new FolderCreateRequest("current-path", invalidName);
        UserEntity userEntity  = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/empty")
                        .flashAttr("folderCreateRequest", invalidCreateRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).createEmptyFolder(userEntity.getId(), invalidCreateRequest);
    }

    @DisplayName("CreateEmptyFolder: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void createEmptyFolder_invalidCurrentFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidCurrentFolderPath) throws Exception {
        FolderCreateRequest invalidCreateRequest = new FolderCreateRequest(invalidCurrentFolderPath, "folder-name");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/empty")
                        .flashAttr("folderCreateRequest", invalidCreateRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).createEmptyFolder(userEntity.getId(), invalidCreateRequest);
    }


    @DisplayName("Delete folder: should delete folder and redirect to home with successMessage flash attribute")
    @Test
    void deleteFolder_validRequest_deletesAndRedirectsToHomeWithSuccessMessage() throws Exception {
        FolderDeleteRequest deleteRequest = new FolderDeleteRequest("test-path");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/delete")
                        .flashAttr("folderDeleteRequest", deleteRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(folderService).deleteFolder(userEntity.getId(), deleteRequest);
    }

    @DisplayName("Delete folder: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void deleteFolder_invalidFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidFolderPath) throws Exception {
        FolderDeleteRequest deleteRequest = new FolderDeleteRequest(invalidFolderPath);
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/delete")
                        .flashAttr("folderDeleteRequest", deleteRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).deleteFolder(userEntity.getId(), deleteRequest);
    }


    @DisplayName("Download folder: should download folder and redirect to home with successMessage flash attribute")
    @Test
    void downloadFolder_validRequest_downloadsWithExpectedHeaderAndContentAndRedirectsToHome() throws Exception {
        FolderDownloadRequest downloadRequest = new FolderDownloadRequest("name", "path");
        UserEntity userEntity = getUserEntity();
        String expectedHeaderValue = "attachment; filename=name.zip";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(folderService.downloadFolder(userEntity.getId(), downloadRequest))
                .thenReturn(outputStream);

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("folderDownloadRequest", downloadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().isOk(),
                header().string(HttpHeaders.CONTENT_DISPOSITION, expectedHeaderValue),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM),
                content().bytes(outputStream.toByteArray())
        );

        verify(folderService).downloadFolder(userEntity.getId(), downloadRequest);
    }


    @DisplayName("Download folder: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderName")
    @ParameterizedTest
    void downloadFolder_invalidFolderName_redirectsToHomeWithValidationErrorMessage(String invalidName) throws Exception {
        FolderDownloadRequest folderDownloadRequest = new FolderDownloadRequest(invalidName, "path");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("folderDownloadRequest", folderDownloadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).downloadFolder(userEntity.getId(), folderDownloadRequest);
    }

    @DisplayName("Download folder: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void downloadFolder_invalidFolderPath_redirectsToHomeWithValidationErrorMessage(String invalidPath) throws Exception {
        FolderDownloadRequest folderDownloadRequest = new FolderDownloadRequest("name", invalidPath);
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("folderDownloadRequest", folderDownloadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(folderService, never()).downloadFolder(userEntity.getId(), folderDownloadRequest);
    }

    private static Stream<Arguments> getInvalidArgsForFolderName() {
        return Stream.of(
                arguments(named("Folder name is blank", "  ")),
                arguments(named("Folder name is null", null))
        );
    }
    private static Stream<Arguments> getInvalidArgsForFolderPath() {
        return Stream.of(
                arguments(named("Current folder path is blank", "  ")),
                arguments(named("Current folder path is null", null))
        );
    }

    private UserEntity getUserEntity() {
        return new UserEntity(1L, "test-username", "test-password");
    }

    private List<MultipartFile> getListWithMockFile() {
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);
        return List.of(mockFile);
    }
}

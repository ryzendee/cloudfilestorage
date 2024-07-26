package com.app.cloudfilestorage.unit.controller;

import com.app.cloudfilestorage.config.TestSecurityConfig;
import com.app.cloudfilestorage.controller.FileController;
import com.app.cloudfilestorage.dto.request.file.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.file.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.file.FileRenameRequest;
import com.app.cloudfilestorage.dto.request.file.FileUploadRequest;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.enums.FlashAttr;
import com.app.cloudfilestorage.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

    private static final String HOME_PAGE = "/";
    private static final String BASE_PATH = "/files";
    private static final String FLASH_ATR_VALIDATION_ERROR_MESSAGE = FlashAttr.VALIDATION_ERROR_MESSAGE.getName();
    private static final String FLASH_ATR_SUCCESS_MESSAGE = FlashAttr.SUCCESS_MESSAGE.getName();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FileService fileService;

    @DisplayName("Upload file: should upload file and redirect to home with successMessage flash attribute")
    @Test
    void uploadFile_validRequest_shouldUploadFileAndRedirectToHomePageWithSuccessMessage() throws Exception {
        MultipartFile mockFile = getMockMultipartFile();
        FileUploadRequest uploadRequest = new FileUploadRequest("/path", mockFile);
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("fileUploadRequest", uploadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(fileService).uploadFile(userEntity.getId(), uploadRequest);
    }

    @DisplayName("Upload file: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void uploadFile_invalidFolderPathInRequest_shouldRedirectToHomePageWithValidationErrorMessage(String folderPath) throws Exception {
        MultipartFile mockFile = getMockMultipartFile();
        FileUploadRequest uploadRequest = new FileUploadRequest(folderPath, mockFile);
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/upload")
                        .flashAttr("fileUploadRequest", uploadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(fileService).uploadFile(userEntity.getId(), uploadRequest);
    }

    @DisplayName("Delete file: should delete and redirect to home with successMessage flash attribute")
    @Test
    void deleteFile_validRequest_shouldDeleteAndRedirectToHomePageWithSuccessMessage() throws Exception {
        FileDeleteRequest deleteRequest = new FileDeleteRequest("/path");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/delete")
                        .flashAttr("fileDeleteRequest", deleteRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(fileService).deleteFile(userEntity.getId(), deleteRequest);
    }

    @DisplayName("Delete file: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void deleteFile_invalidFolderPath_shouldRedirectToHomeWithValidationErrorMessage(String invalidFolderPath) throws Exception {
        FileDeleteRequest deleteRequest = new FileDeleteRequest(invalidFolderPath);
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/delete")
                        .flashAttr("fileDeleteRequest", deleteRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(fileService, never()).deleteFile(userEntity.getId(), deleteRequest);
    }

    @DisplayName("Download file: should download file with expected response data and then redirect to home")
    @Test
    void downloadFile_validRequest_shouldDownloadWithExpectedHeaderAndContentAndRedirectsToHome() throws Exception {
        FileDownloadRequest downloadRequest = new FileDownloadRequest("name", "/path");
        UserEntity userEntity = getUserEntity();
        String expectedHeaderValue = "attachment; filename=name";
        Resource resource = new ByteArrayResource(expectedHeaderValue.getBytes());

        when(fileService.downloadFile(userEntity.getId(), downloadRequest))
                .thenReturn(resource);

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("fileDownloadRequest", downloadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().isOk(),
                header().string(HttpHeaders.CONTENT_DISPOSITION, expectedHeaderValue),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM),
                content().bytes(resource.getContentAsByteArray())
        );

        verify(fileService).downloadFile(userEntity.getId(), downloadRequest);
    }

    @DisplayName("Download file: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFileName")
    @ParameterizedTest
    void downloadFile_invalidFileName_shouldRedirectToHomeWithValidationErrorMessage(String invalidName) throws Exception {
        FileDownloadRequest downloadRequest = new FileDownloadRequest(invalidName, "path");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("fileDownloadRequest", downloadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(fileService, never()).downloadFile(userEntity.getId(), downloadRequest);
    }

    @DisplayName("Download file: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFolderPath")
    @ParameterizedTest
    void downloadFile_invalidFolderPath_shouldRedirectToHomeWithValidationErrorMessage(String invalidPath) throws Exception {
        FileDownloadRequest downloadRequest = new FileDownloadRequest("name", invalidPath);
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/download")
                        .flashAttr("fileDownloadRequest", downloadRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(fileService, never()).downloadFile(userEntity.getId(), downloadRequest);
    }

    @DisplayName("Rename file: should rename file")
    @Test
    void renameFile_validRequest_shouldRenameAndRedirectToHomePageWithSuccessMessage() throws Exception {
        FileRenameRequest renameRequest = new FileRenameRequest("updated", "path", "txt");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/rename")
                        .flashAttr("fileRenameRequest", renameRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_SUCCESS_MESSAGE)
        );

        verify(fileService).renameFile(userEntity.getId(), renameRequest);
    }

    @DisplayName("Rename file: should redirect to home with validationErrorMessage flash attribute")
    @MethodSource("getInvalidArgsForFileName")
    @ParameterizedTest
    void renameFile_invalidUpdatedName_shouldRedirectToHomeWithValidationErrorMessage(String updatedName) throws Exception {
        FileRenameRequest renameRequest = new FileRenameRequest(updatedName, "path", "txt");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/rename")
                        .flashAttr("fileRenameRequest", renameRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(fileService, never()).renameFile(userEntity.getId(), renameRequest);
    }

    @DisplayName("Rename file: should redirect to home with validationErrorMessage flash attribute")
    @NullSource
    @EmptySource
    @ParameterizedTest
    void renameFile_invalidFilePath_shouldRedirectToHomeWithValidationErrorMessage(String filePath) throws Exception {
        FileRenameRequest renameRequest = new FileRenameRequest("updated", filePath, "txt");
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                post(BASE_PATH + "/rename")
                        .flashAttr("fileRenameRequest", renameRequest)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl(HOME_PAGE),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE)
        );

        verify(fileService, never()).renameFile(userEntity.getId(), renameRequest);
    }

    private static Stream<Arguments> getInvalidArgsForFileName() {
        return Stream.of(
                arguments(named("File name is blank", "  ")),
                arguments(named("File name is null", null))
        );
    }
    private static Stream<Arguments> getInvalidArgsForFolderPath() {
        return Stream.of(
                arguments(named("Current folder path is blank", "  ")),
                arguments(named("Current folder path is null", null))
        );
    }

    private MultipartFile getMockMultipartFile() {
        return new MockMultipartFile("file", new byte[0]);
    }

    private UserEntity getUserEntity() {
        return new UserEntity(1L, "test-username", "test-password");
    }

}

package com.app.cloudfilestorage.unit.service;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.folder.*;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.exception.FolderServiceException;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.FolderUploadReqToMinioSaveDtoListMapper;
import com.app.cloudfilestorage.mapper.MinioObjectToFolderResponseMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioFolderRepository;
import com.app.cloudfilestorage.service.impl.FolderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

import static com.app.cloudfilestorage.utils.FolderNameUtil.renameLastFolderInPath;
import static com.app.cloudfilestorage.utils.PathGeneratorUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

    private static final Long USER_ID = 1L;
    @InjectMocks
    private FolderServiceImpl folderService;
    @Mock
    private MinioFolderRepository minioFolderRepository;
    @Mock
    private MinioObjectToFolderResponseMapper minioObjectToFolderResponseMapper;
    @Mock
    private FolderUploadReqToMinioSaveDtoListMapper folderUploadReqToMinioSaveDtoListMapper;

    @DisplayName("Get all folders by userId: should return folders")
    @Test
    void getAllFoldersByUserId_existsFolders_returnsFolders() {
        String path = formatBasePath(USER_ID);
        MinioObject minioObject = new MinioObject("test-minio-obj", ZonedDateTime.now(), 0L);
        FolderResponse folderResponse = new FolderResponse("test-name", "/path", "0 KB");
        var listOfMinioObjects = List.of(minioObject);

        when(minioFolderRepository.findAllFoldersByPathRecursive(path))
                .thenReturn(listOfMinioObjects);
        when(minioObjectToFolderResponseMapper.map(USER_ID, minioObject))
                .thenReturn(folderResponse);

        var actualFolderResponseList = folderService.getAllFoldersByUserId(USER_ID);

        verify(minioFolderRepository).findAllFoldersByPathRecursive(path);
        verify(minioObjectToFolderResponseMapper).map(USER_ID, minioObject);
        assertThat(actualFolderResponseList).containsOnly(folderResponse);
    }

    @DisplayName("Get all folders by userId: throws FolderServiceEx")
    @Test
    void getAllFoldersByUserId_repositoryThrowsMinioRepositoryException_shouldThrowFolderServiceEx() {
        String path = formatBasePath(USER_ID);

        when(minioFolderRepository.findAllFoldersByPathRecursive(path))
                .thenThrow(new MinioRepositoryException());

        assertThatThrownBy(() -> folderService.getAllFoldersByUserId(USER_ID))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).findAllFoldersByPathRecursive(path);
    }

    @DisplayName("Get folders for path by userId: should return folders")
    @Test
    void getFoldersForPathByUserId_existsFolders_returnsFolders() {
        String argPath = "test-path";
        String formattedPath = formatPathForFolder(USER_ID, argPath);
        MinioObject minioObject = new MinioObject("test-minio-obj", ZonedDateTime.now(), 0L);
        FolderResponse folderResponse = new FolderResponse("test-name", "/path", "0 KB");
        var listOfMinioObjects = List.of(minioObject);

        when(minioFolderRepository.findAllFoldersByPath(formattedPath))
                .thenReturn(listOfMinioObjects);
        when(minioObjectToFolderResponseMapper.map(USER_ID, minioObject))
                .thenReturn(folderResponse);

        var actualFolderResponseList = folderService.getFoldersForPathByUserId(USER_ID, argPath);

        verify(minioFolderRepository).findAllFoldersByPath(formattedPath);
        verify(minioObjectToFolderResponseMapper).map(USER_ID, minioObject);
        assertThat(actualFolderResponseList).containsOnly(folderResponse);
    }

    @DisplayName("Get folders for path by userId: throws FolderServiceEx")
    @Test
    void getFoldersForPathByUserId_repositoryThrowsMinioRepositoryException_shouldThrowFolderServiceEx() {
        String argPath = "test-path";
        String formattedPath = formatPathForFolder(USER_ID, argPath);

        when(minioFolderRepository.findAllFoldersByPath(formattedPath))
                .thenThrow(new MinioRepositoryException());

        assertThatThrownBy(() -> folderService.getFoldersForPathByUserId(USER_ID, argPath))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).findAllFoldersByPath(formattedPath);
    }

    @DisplayName("Upload folder: should upload folder")
    @Test
    void uploadFolder_validRequest_shouldUpload() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("test-file");
        FolderUploadRequest uploadRequest = new FolderUploadRequest("test-path", List.of(mockFile));
        MinioSaveDataDto saveData = getMinioSaveDataFromMockFile(mockFile);
        var listOfSaveData = List.of(saveData);

        when(folderUploadReqToMinioSaveDtoListMapper.map(USER_ID, uploadRequest))
                .thenReturn(listOfSaveData);
        doNothing()
                .when(minioFolderRepository).saveAll(listOfSaveData);

        folderService.uploadFolder(USER_ID, uploadRequest);

        verify(folderUploadReqToMinioSaveDtoListMapper).map(USER_ID, uploadRequest);
        verify(minioFolderRepository).saveAll(listOfSaveData);
    }

    @DisplayName("Upload folder: throws FolderServiceEx")
    @Test
    void uploadFolder_repositoryThrowsMinioRepositoryEx_shouldThrowFolderServiceEx() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("test-file");
        FolderUploadRequest uploadRequest = new FolderUploadRequest("test-path", List.of(mockFile));
        MinioSaveDataDto saveData = getMinioSaveDataFromMockFile(mockFile);
        var listOfSaveData = List.of(saveData);

        when(folderUploadReqToMinioSaveDtoListMapper.map(USER_ID, uploadRequest))
                .thenReturn(listOfSaveData);
        doThrow(new MinioRepositoryException())
                .when(minioFolderRepository).saveAll(listOfSaveData);

        assertThatThrownBy(() -> folderService.uploadFolder(USER_ID, uploadRequest))
                .isInstanceOf(FolderServiceException.class);

        verify(folderUploadReqToMinioSaveDtoListMapper).map(USER_ID, uploadRequest);
        verify(minioFolderRepository).saveAll(listOfSaveData);
    }

    @DisplayName("Delete folder: should delete folder")
    @Test
    void deleteFolder_existsFolder_shouldDeleteFolder() {
        FolderDeleteRequest deleteRequest = new FolderDeleteRequest("test-path");
        String formattedPath = formatPathForFolder(USER_ID, deleteRequest.getFolderPath());

        doNothing()
                .when(minioFolderRepository).deleteFolderByPath(formattedPath);

        folderService.deleteFolder(USER_ID, deleteRequest);

        verify(minioFolderRepository).deleteFolderByPath(formattedPath);
    }

    @DisplayName("Delete folder: throws FolderServiceEx")
    @Test
    void deleteFolder_repositoryThrowsMinioRepositoryEx_shouldThrowFolderServiceEx() {
        FolderDeleteRequest deleteRequest = new FolderDeleteRequest("test-path");
        String formattedPath = formatPathForFolder(USER_ID, deleteRequest.getFolderPath());

        doThrow(new MinioRepositoryException())
                .when(minioFolderRepository).deleteFolderByPath(formattedPath);

        assertThatThrownBy(() -> folderService.deleteFolder(USER_ID, deleteRequest))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).deleteFolderByPath(formattedPath);
    }

    @DisplayName("Create empty folder: should create empty folder")
    @Test
    void createEmptyFolder_validRequest_shouldCreateEmptyFolder() {
        FolderCreateRequest createRequest = new FolderCreateRequest("test-path", "test-name");
        String folderPath = formatPathForFolder(USER_ID, createRequest.getCurrentFolderPath(), createRequest.getFolderName());

        doNothing()
                .when(minioFolderRepository).createEmptyFolder(folderPath);

        folderService.createEmptyFolder(USER_ID, createRequest);

        verify(minioFolderRepository).createEmptyFolder(folderPath);
    }

    @DisplayName("Create empty folder: throws FolderServiceEx because repository throw MinioRepositoryEx")
    @Test
    void createEmptyFolder_repositoryThrowMinioRepositoryEx_shouldThrowFolderServiceEx() {
        FolderCreateRequest createRequest = new FolderCreateRequest("test-path", "test-name");
        String folderPath = formatPathForFolder(USER_ID, createRequest.getCurrentFolderPath(), createRequest.getFolderName());

        doThrow(new MinioRepositoryException())
                .when(minioFolderRepository).createEmptyFolder(folderPath);

        assertThatThrownBy(() -> folderService.createEmptyFolder(USER_ID, createRequest))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).createEmptyFolder(folderPath);
    }

    @DisplayName("Create empty folder: throws FolderServiceEx because repository throws MinioObjectExistsEx")
    @Test
    void createEmptyFolder_repositoryThrowMinioObjectExistsEx_shouldThrowFolderServiceEx() {
        FolderCreateRequest createRequest = new FolderCreateRequest("test-path", "test-name");
        String folderPath = formatPathForFolder(USER_ID, createRequest.getCurrentFolderPath(), createRequest.getFolderName());

        doThrow(new MinioObjectExistsException())
                .when(minioFolderRepository).createEmptyFolder(folderPath);

        assertThatThrownBy(() -> folderService.createEmptyFolder(USER_ID, createRequest))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).createEmptyFolder(folderPath);
    }

    @DisplayName("Download folder: should download folder")
    @Test
    void downloadFolder_existsFolder_shouldDownloadFolder() {
        FolderDownloadRequest downloadRequest = new FolderDownloadRequest("test-name", "test-path");
        String formattedPath = formatPathForFolder(USER_ID, downloadRequest.getFolderPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        when(minioFolderRepository.downloadFolderByPath(formattedPath))
                .thenReturn(baos);

        ByteArrayOutputStream actualBaos = folderService.downloadFolder(USER_ID, downloadRequest);

        verify(minioFolderRepository).downloadFolderByPath(formattedPath);
        assertThat(actualBaos).isNotNull();
    }

    @DisplayName("Download folder: throws FolderServiceEx")
    @Test
    void downloadFolder_repositoryThrowMinioRepositoryEx_shouldThrowFolderServiceEx() {
        FolderDownloadRequest downloadRequest = new FolderDownloadRequest("test-name", "test-path");
        String formattedPath = formatPathForFolder(USER_ID, downloadRequest.getFolderPath());

        when(minioFolderRepository.downloadFolderByPath(formattedPath))
                .thenThrow(new MinioRepositoryException());

        assertThatThrownBy(() -> folderService.downloadFolder(USER_ID, downloadRequest))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).downloadFolderByPath(formattedPath);
    }

    @DisplayName("Rename folder: should rename folder")
    @Test
    void renameFolder_nonExistsName_shouldRenameFolder() {
        FolderRenameRequest renameRequest = new FolderRenameRequest("updated-name", "current-name", "path");
        String pathWithUpdatedName = renameLastFolderInPath(renameRequest.getPath(), renameRequest.getUpdatedName());
        String formattedPathWithUpdatedName = formatPathForFolder(USER_ID, pathWithUpdatedName);
        String formattedPathWithCurrentName = formatPathForFolder(USER_ID, renameRequest.getPath());

        doNothing()
                .when(minioFolderRepository).renameFolder(formattedPathWithCurrentName, formattedPathWithUpdatedName);

        folderService.renameFolder(USER_ID, renameRequest);

        verify(minioFolderRepository).renameFolder(formattedPathWithCurrentName, formattedPathWithUpdatedName);
    }

    @DisplayName("Rename folder: throws FolderServiceEx because repository throws MinioRepositoryEx")
    @Test
    void renameFolder_repositoryThrowsMinioRepositoryEx_shouldThrowFOlderServiceEx() {
        FolderRenameRequest renameRequest = new FolderRenameRequest("updated-name", "current-name", "path");
        String pathWithUpdatedName = renameLastFolderInPath(renameRequest.getPath(), renameRequest.getUpdatedName());
        String formattedPathWithUpdatedName = formatPathForFolder(USER_ID, pathWithUpdatedName);
        String formattedPathWithCurrentName = formatPathForFolder(USER_ID, renameRequest.getPath());

        doThrow(new MinioRepositoryException())
                .when(minioFolderRepository).renameFolder(formattedPathWithCurrentName, formattedPathWithUpdatedName);

        assertThatThrownBy(() -> folderService.renameFolder(USER_ID, renameRequest))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).renameFolder(formattedPathWithCurrentName, formattedPathWithUpdatedName);
    }

    @DisplayName("Rename folder: throws FolderServiceEx because repository throws MinioObjectExistsEx")
    @Test
    void renameFolder_repositoryThrowsMinioObjectExistsEx_shouldThrowFolderServiceEx() {
        FolderRenameRequest renameRequest = new FolderRenameRequest("updated-name", "current-name", "path");
        String pathWithUpdatedName = renameLastFolderInPath(renameRequest.getPath(), renameRequest.getUpdatedName());
        String formattedPathWithUpdatedName = formatPathForFolder(USER_ID, pathWithUpdatedName);
        String formattedPathWithCurrentName = formatPathForFolder(USER_ID, renameRequest.getPath());

        doThrow(new MinioRepositoryException())
                .when(minioFolderRepository).renameFolder(formattedPathWithCurrentName, formattedPathWithUpdatedName);

        assertThatThrownBy(() -> folderService.renameFolder(USER_ID, renameRequest))
                .isInstanceOf(FolderServiceException.class);

        verify(minioFolderRepository).renameFolder(formattedPathWithCurrentName, formattedPathWithUpdatedName);
    }

    private MultipartFile getMockMultipartFileWithName(String name) {
        return new MockMultipartFile(
                "file",
                name,
                "text/plain",
                "Content".getBytes(StandardCharsets.UTF_8)
        );
    }

    private MinioSaveDataDto getMinioSaveDataFromMockFile(MultipartFile mockFile) throws Exception {
        return new MinioSaveDataDto(mockFile.getOriginalFilename(), mockFile.getInputStream(), mockFile.getSize());
    }
}

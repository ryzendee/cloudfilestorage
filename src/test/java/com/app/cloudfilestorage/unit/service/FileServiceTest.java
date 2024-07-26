package com.app.cloudfilestorage.unit.service;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.file.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.file.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.file.FileRenameRequest;
import com.app.cloudfilestorage.dto.request.file.FileUploadRequest;
import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.exception.FileServiceException;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.FileUploadRequestToMinioSaveDataMapper;
import com.app.cloudfilestorage.mapper.MinioObjectToFileResponseMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioFileRepository;
import com.app.cloudfilestorage.service.impl.FileServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

import static com.app.cloudfilestorage.utils.DateFormatterUtil.formatZonedDateTime;
import static com.app.cloudfilestorage.utils.FileNameUtil.renameFileInPath;
import static com.app.cloudfilestorage.utils.PathGeneratorUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    private static final Long USER_ID = 1L;
    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private MinioFileRepository minioFileRepository;
    @Mock
    private MinioObjectToFileResponseMapper minioObjectToFileResponseMapper;
    @Mock
    private FileUploadRequestToMinioSaveDataMapper minioSaveDataMapper;


    @DisplayName("Get all files by userId: should return files")
    @Test
    void getAllFilesByUserId_existsFiles_returnsFiles() {
        String path = formatBasePath(USER_ID);
        MinioObject minioObject = new MinioObject("test-minio-obj", ZonedDateTime.now(), 0L);
        FileResponse fileResponse = getFileResponseWithName("test-folder");
        var listOfMinioObjects = List.of(minioObject);

        when(minioFileRepository.findAllFilesByPathRecursive(path))
                .thenReturn(listOfMinioObjects);
        when(minioObjectToFileResponseMapper.map(USER_ID, minioObject))
                .thenReturn(fileResponse);

        var actualFileResponseList = fileService.getAllFilesByUserId(USER_ID);
        verify(minioFileRepository).findAllFilesByPathRecursive(path);
        verify(minioObjectToFileResponseMapper).map(USER_ID, minioObject);
        assertThat(actualFileResponseList).containsOnly(fileResponse);
    }

    @DisplayName("Get all files by userId: throws FileServiceEx")
    @Test
    void getAllFilesByUserId_repositoryThrowsMinioRepositoryException_shouldThrowFileServiceEx() {
        String path = formatBasePath(USER_ID);

        when(minioFileRepository.findAllFilesByPathRecursive(path))
                .thenThrow(new MinioRepositoryException());

        assertThatThrownBy(() -> fileService.getAllFilesByUserId(USER_ID))
                .isInstanceOf(FileServiceException.class);

        verify(minioFileRepository).findAllFilesByPathRecursive(path);
    }

    @DisplayName("Get files for path by userId: should return files")
    @Test
    void getFilesForPathByUserId_existsFiles_returnsFiles() {
        String argPath = "test-path";
        String formattedPath = formatPathForFolder(USER_ID, argPath);
        MinioObject minioObject = new MinioObject("test-minio-obj", ZonedDateTime.now(), 0L);
        FileResponse fileResponse = getFileResponseWithName("test-folder");
        var listOfMinioObjects = List.of(minioObject);

        when(minioFileRepository.findAllFilesByPath(formattedPath))
                .thenReturn(listOfMinioObjects);
        when(minioObjectToFileResponseMapper.map(USER_ID, minioObject))
                .thenReturn(fileResponse);

        var actualFileResponseList = fileService.getFilesForPathByUserId(USER_ID, argPath);
        verify(minioFileRepository).findAllFilesByPath(formattedPath);
        verify(minioObjectToFileResponseMapper).map(USER_ID, minioObject);
        assertThat(actualFileResponseList).containsOnly(fileResponse);
    }

    @DisplayName("Get files for path by userId: throws FileServiceEx")
    @Test
    void getFilesForPathByUserId_repositoryThrowsMinioRepositoryException_shouldThrowFileServiceEx() {
        String argPath = "test-path";
        String formattedPath = formatPathForFolder(USER_ID, argPath);

        when(minioFileRepository.findAllFilesByPath(formattedPath))
                .thenThrow(new MinioRepositoryException());

        assertThatThrownBy(() -> fileService.getFilesForPathByUserId(USER_ID, argPath))
                .isInstanceOf(FileServiceException.class);

        verify(minioFileRepository).findAllFilesByPath(formattedPath);
    }

    @DisplayName("Upload file: should upload file")
    @Test
    void uploadFile_validRequest_shouldUpload() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("test-file");
        FileUploadRequest uploadRequest = new FileUploadRequest("test-path", mockFile);
        String formattedPath = formatPathForFolder(USER_ID, uploadRequest.getCurrentFolderPath());
        MinioSaveDataDto saveData = getMinioSaveDataFromMockFile(mockFile);

        when(minioSaveDataMapper.map(formattedPath, uploadRequest))
                .thenReturn(saveData);
        doNothing()
                .when(minioFileRepository).saveFile(saveData);

        fileService.uploadFile(USER_ID, uploadRequest);
        verify(minioSaveDataMapper).map(formattedPath, uploadRequest);
        verify(minioFileRepository).saveFile(saveData);
    }

    @DisplayName("Upload file: throws FileServiceEx")
    @Test
    void uploadFile_repositoryThrowsMinioRepositoryEx_shouldThrowFileServiceEx() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("test-file");
        FileUploadRequest uploadRequest = new FileUploadRequest("test-path", mockFile);
        String formattedPath = formatPathForFolder(USER_ID, uploadRequest.getCurrentFolderPath());
        MinioSaveDataDto saveData = getMinioSaveDataFromMockFile(mockFile);

        when(minioSaveDataMapper.map(formattedPath, uploadRequest))
                .thenReturn(saveData);
        doThrow(new MinioRepositoryException())
                .when(minioFileRepository).saveFile(saveData);

        assertThatThrownBy(() -> fileService.uploadFile(USER_ID, uploadRequest))
                .isInstanceOf(FileServiceException.class);

        verify(minioSaveDataMapper).map(formattedPath, uploadRequest);
        verify(minioFileRepository).saveFile(saveData);
    }

    @DisplayName("Delete file: should delete file")
    @Test
    void deleteFile_existsFile_shouldDeleteFile() {
        FileDeleteRequest deleteRequest = new FileDeleteRequest("test-path");
        String formattedPath = formatPathForFile(USER_ID, deleteRequest.getPath());

        doNothing()
                .when(minioFileRepository).deleteFileByObjectName(formattedPath);

        fileService.deleteFile(USER_ID, deleteRequest);
        verify(minioFileRepository).deleteFileByObjectName(formattedPath);
    }

    @DisplayName("Delete file: throws FileServiceEx")
    @Test
    void deleteFile_repositoryThrowsMinioRepositoryEx_shouldThrowFileServiceEx() {
        FileDeleteRequest deleteRequest = new FileDeleteRequest("test-path");
        String formattedPath = formatPathForFile(USER_ID, deleteRequest.getPath());

        doThrow(new MinioRepositoryException())
                .when(minioFileRepository).deleteFileByObjectName(formattedPath);

        assertThatThrownBy(() -> fileService.deleteFile(USER_ID, deleteRequest))
                .isInstanceOf(FileServiceException.class);

        verify(minioFileRepository).deleteFileByObjectName(formattedPath);
    }

    @DisplayName("Download file: should download file")
    @Test
    void downloadFile_existsFile_shouldDownloadFile() throws Exception {
        FileDownloadRequest downloadRequest = new FileDownloadRequest("filename", "test-path");
        String formattedPath = formatPathForFile(USER_ID, downloadRequest.getPath());
        byte[] content = formattedPath.getBytes(StandardCharsets.UTF_8);
        InputStream bais = new ByteArrayInputStream(content);

        when(minioFileRepository.downloadFileByObjectName(formattedPath))
                .thenReturn(bais);

        Resource resource = fileService.downloadFile(USER_ID, downloadRequest);
        verify(minioFileRepository).downloadFileByObjectName(formattedPath);
        assertThat(resource).isNotNull();
        assertThat(resource.getContentAsByteArray()).containsOnly(content);

    }

    @DisplayName("Download file: throws FileServiceEx")
    @Test
    void downloadFile_repositoryThrowsMinioRepositoryEx_shouldThrowFileServiceEx() {
        FileDownloadRequest downloadRequest = new FileDownloadRequest("filename", "test-path");
        String formattedPath = formatPathForFile(USER_ID, downloadRequest.getPath());

        when(minioFileRepository.downloadFileByObjectName(formattedPath))
                .thenThrow(new MinioRepositoryException());

        assertThatThrownBy(() -> fileService.downloadFile(USER_ID, downloadRequest))
                .isInstanceOf(FileServiceException.class);

        verify(minioFileRepository).downloadFileByObjectName(formattedPath);
    }

    @DisplayName("Rename file: throws FileServiceEx")
    @Test
    void renameFile_nonExistsName_shouldRenameFile() {
        FileRenameRequest renameRequest = new FileRenameRequest("updated-name", "path", "txt");
        String pathWithUpdatedName = renameFileInPath(renameRequest.getPath(), renameRequest.getUpdatedName());
        String formattedPathWithUpdatedName = formatPathForFile(USER_ID, pathWithUpdatedName);
        String formattedPathWithCurrentName = formatPathForFile(USER_ID, renameRequest.getPath());

        doNothing()
                .when(minioFileRepository).renameFile(formattedPathWithCurrentName, formattedPathWithUpdatedName);

        fileService.renameFile(USER_ID, renameRequest);

        verify(minioFileRepository).renameFile(formattedPathWithCurrentName, formattedPathWithUpdatedName);
    }

    @DisplayName("Rename file: throws FileServiceEx because repository throws MinioRepositoryEx")
    @Test
    void renameFile_repositoryThrowsMinioRepositoryEx_shouldThrowFileServiceEx() {
        FileRenameRequest renameRequest = new FileRenameRequest("updated-name", "path", "txt");
        String pathWithUpdatedName = renameFileInPath(renameRequest.getPath(), renameRequest.getUpdatedName());
        String formattedPathWithUpdatedName = formatPathForFile(USER_ID, pathWithUpdatedName);
        String formattedPathWithCurrentName = formatPathForFile(USER_ID, renameRequest.getPath());

        doThrow(new MinioRepositoryException())
                .when(minioFileRepository).renameFile(formattedPathWithCurrentName, formattedPathWithUpdatedName);

        assertThatThrownBy(() -> fileService.renameFile(USER_ID, renameRequest))
                .isInstanceOf(FileServiceException.class);

        verify(minioFileRepository).renameFile(formattedPathWithCurrentName, formattedPathWithUpdatedName);
    }

    @DisplayName("Rename file: throws FileServiceEx because repository throws MinioObjectExistsEx")
    @Test
    void renameFile_repositoryThrowsMinioObjectExistsEx_shouldThrowFileServiceEx() {
        FileRenameRequest renameRequest = new FileRenameRequest("updated-name", "path", "txt");
        String pathWithUpdatedName = renameFileInPath(renameRequest.getPath(), renameRequest.getUpdatedName());
        String formattedPathWithUpdatedName = formatPathForFile(USER_ID, pathWithUpdatedName);
        String formattedPathWithCurrentName = formatPathForFile(USER_ID, renameRequest.getPath());

        doThrow(new MinioObjectExistsException())
                .when(minioFileRepository).renameFile(formattedPathWithCurrentName, formattedPathWithUpdatedName);

        assertThatThrownBy(() -> fileService.renameFile(USER_ID, renameRequest))
                .isInstanceOf(FileServiceException.class);

        verify(minioFileRepository).renameFile(formattedPathWithCurrentName, formattedPathWithUpdatedName);
    }

    private FileResponse getFileResponseWithName(String name) {
        return new FileResponse(name,
                "/test-path",
                "txt",
                "0 KB",
                formatZonedDateTime(ZonedDateTime.now())
        );
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



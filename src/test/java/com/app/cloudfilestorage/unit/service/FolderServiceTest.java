package com.app.cloudfilestorage.unit.service;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.dto.request.*;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.mapper.FolderUploadReqToMinioSaveDtoListMapper;
import com.app.cloudfilestorage.mapper.MinioObjectToFolderResponseMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioFolderRepository;
import com.app.cloudfilestorage.service.impl.FolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.app.cloudfilestorage.utils.PathGeneratorUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
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


    @Test
    void getFolderListForPathByUserId_existsIdAndPath_returnsListWithExpectedMinioObject() {
        String path = "path/name";
        String formattedPath = formatPath(USER_ID, path);
        List<MinioObject> minioObjectList = List.of(new MinioObject(path, true, 0));
        FolderResponse folderResponse = new FolderResponse("name", path, 0);

        when(minioFolderRepository.findAllFoldersByPath(formattedPath))
                .thenReturn(minioObjectList);
        when(minioObjectToFolderResponseMapper.map(minioObjectList.getFirst(), USER_ID))
                .thenReturn(folderResponse);

        List<FolderResponse> folderResponseList = folderService.getFoldersForPathByUserId(USER_ID, path);
        assertThat(folderResponseList).containsOnly(folderResponse);
        verify(minioFolderRepository).findAllFoldersByPath(formattedPath);
        verify(minioObjectToFolderResponseMapper).map(minioObjectList.getFirst(), USER_ID);
    }

    @Test
    void createEmptyFolder_validRequest_createsSuccessfully() {
        FolderCreateRequest createRequest = new FolderCreateRequest("path/folder", "name");
        String formattedPath = formatPathForFolder(USER_ID, createRequest.getCurrentFolderPath(), createRequest.getFolderName());

        doNothing()
                .when(minioFolderRepository).createEmptyFolder(formattedPath);

        folderService.createEmptyFolder(USER_ID, createRequest);
        verify(minioFolderRepository).createEmptyFolder(formattedPath);
    }

    @Test
    void uploadFolder_validRequest_uploadsSuccessfully() {
        FolderUploadRequest uploadRequest = new FolderUploadRequest();
        MinioSaveDataDto saveDataDto = new MinioSaveDataDto("path/name", null, 0);
        List<MinioSaveDataDto> saveDataDtoList = List.of(saveDataDto);

        when(folderUploadReqToMinioSaveDtoListMapper.map(USER_ID, uploadRequest))
                .thenReturn(saveDataDtoList);
        doNothing()
                .when(minioFolderRepository).saveAll(saveDataDtoList);

        folderService.uploadFolder(USER_ID, uploadRequest);

        verify(folderUploadReqToMinioSaveDtoListMapper).map(USER_ID, uploadRequest);
        verify(minioFolderRepository).saveAll(saveDataDtoList);
    }

    @Test
    void deleteFolder_validRequest_deletesSuccessfully() {
        FolderDeleteRequest deleteRequest = new FolderDeleteRequest("/path/");
        String formattedPath = formatPath(USER_ID, deleteRequest.getFolderPath());

        doNothing()
                .when(minioFolderRepository).deleteFolderByPath(formattedPath);

        folderService.deleteFolder(USER_ID, deleteRequest);

        verify(minioFolderRepository).deleteFolderByPath(formattedPath);
    }

    @Test
    void downloadFolder_validRequest_downloadsSuccessfully() {
        FolderDownloadRequest downloadRequest = new FolderDownloadRequest("name", "path/name");
        String formattedPath = formatPath(USER_ID, downloadRequest.getFolderPath());
        ByteArrayOutputStream baos = mock(ByteArrayOutputStream.class);

        when(minioFolderRepository.downloadFolderByPath(formattedPath))
                .thenReturn(baos);

        ByteArrayOutputStream actualBaos = folderService.downloadFolder(USER_ID, downloadRequest);
        assertThat(baos).isEqualTo(actualBaos);
        verify(minioFolderRepository).downloadFolderByPath(formattedPath);
    }

    @Test
    void renameFolder_validRequest_renameSuccessfully() {
        FolderRenameRequest renameRequest = new FolderRenameRequest("updatedName", "currentName", "folder/currentName/");
        String oldPath = formatPath(USER_ID, renameRequest.getPath());
        String updatedPath = updateFolderPath(USER_ID, renameRequest);

        doNothing()
                .when(minioFolderRepository).renameFolder(oldPath, updatedPath);

        folderService.renameFolder(USER_ID, renameRequest);

        verify(minioFolderRepository).renameFolder(oldPath, updatedPath);
    }

}

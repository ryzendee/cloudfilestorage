package com.app.cloudfilestorage.unit.service;

import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.dto.response.FolderResponse;
import com.app.cloudfilestorage.dto.response.SearchResultResponse;
import com.app.cloudfilestorage.service.FileService;
import com.app.cloudfilestorage.service.FolderService;
import com.app.cloudfilestorage.service.impl.CloudStorageSearchServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    private static final Long USER_ID = 1L;
    @InjectMocks
    private CloudStorageSearchServiceImpl searchService;

    @Mock
    private FileService fileService;

    @Mock
    private FolderService folderService;

    @DisplayName("Search service: should return lists with responses that contains query in path")
    @Test
    void searchByQuery_noCondition_shouldReturnListOfResponsesWithQueryInPath() {
        String query = "/folder";
        var folders = getListOfFolderResponse();
        var files = getListOfFileResponse();

        when(folderService.getAllFoldersByUserId(USER_ID))
                .thenReturn(folders);
        when(fileService.getAllFilesByUserId(USER_ID))
                .thenReturn(files);

        SearchResultResponse searchResult = searchService.searchByQuery(USER_ID, query);

        verify(folderService).getAllFoldersByUserId(USER_ID);
        verify(fileService).getAllFilesByUserId(USER_ID);

        assertThat(searchResult.fileList()).extracting(FileResponse::path)
                .contains(query);
        assertThat(searchResult.folderList()).extracting(FolderResponse::path)
                .contains(query);
    }

    private List<FolderResponse> getListOfFolderResponse() {
        FolderResponse firstResponse = new FolderResponse("folder", "/folder", "15 MB");
        FolderResponse secondResponse = new FolderResponse("folder", "/folder/subfolder", "15 MB");
        FolderResponse thirdResponse = new FolderResponse("folder", "/another-folder", "15 MB");
        FolderResponse fourthResponse = new FolderResponse("folder", "/another-folder/subfolder", "15 MB");

        return List.of(firstResponse, secondResponse, thirdResponse, fourthResponse);
    }

    private List<FileResponse> getListOfFileResponse() {
        FileResponse firstResponse = new FileResponse("file", "/folder", "txt", "15 KB", "2022-02-02");
        FileResponse secondResponse = new FileResponse("file", "/folder/suboflder", "txt", "15 KB", "2022-02-02");
        FileResponse thirdResponse = new FileResponse("file", "/another-folder", "txt", "15 KB", "2022-02-02");
        FileResponse fourthResponse = new FileResponse("file", "/another-folder/subfolder", "txt", "15 KB", "2022-02-02");

        return List.of(firstResponse, secondResponse, thirdResponse, fourthResponse);
    }}

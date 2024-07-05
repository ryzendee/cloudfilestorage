package com.app.cloudfilestorage.dto.response;

import java.util.List;

public record SearchResultResponse(
        List<FolderResponse> folderList,
        List<FileResponse> fileList
) {
}

package com.app.cloudfilestorage.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FolderCreateRequest {
    private String currentFolderPath;
    private String folderName;

    public FolderCreateRequest(String currentFolderPath) {
        this.currentFolderPath = currentFolderPath;
    }
}


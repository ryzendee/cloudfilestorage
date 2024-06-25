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
    private Long ownerId;

    public FolderCreateRequest(Long ownerId, String currentFolderPath) {
        this.ownerId = ownerId;
        this.currentFolderPath = currentFolderPath;
    }
}


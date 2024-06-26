package com.app.cloudfilestorage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FolderDownloadRequest {

    private Long ownerId;
    private String name;
    private String folderPath;

    public FolderDownloadRequest(Long ownerId) {
        this.ownerId = ownerId;
    }
}

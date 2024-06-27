package com.app.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FolderCreateRequest {
    @NotBlank(message = "Current folder path must not be blank")
    private String currentFolderPath;
    @NotBlank(message = "Folder name must not be blank")
    private String folderName;

    public FolderCreateRequest(String currentFolderPath) {
        this.currentFolderPath = currentFolderPath;
    }
}


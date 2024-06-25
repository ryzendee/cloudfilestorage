package com.app.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FolderDeleteRequest {
    @NotNull(message = "User is not authorized")
    private Long ownerId;
    @NotBlank(message = "Folder path must not be blank or null")
    private String folderPath;

    public FolderDeleteRequest(Long ownerId) {
        this.ownerId = ownerId;
    }
}

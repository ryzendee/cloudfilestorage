package com.app.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FolderDeleteRequest {
    @NotBlank(message = "Folder path must not be blank or null")
    private String folderPath;

}

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
public class FolderRenameRequest {

    @NotBlank(message = "Updated folder name must not be blank")
    private String updatedName;
    @NotBlank(message = "Current folder name must not be blank")
    private String currentName;
    @NotBlank(message = "Folder path must not be blank")
    private String path;

}

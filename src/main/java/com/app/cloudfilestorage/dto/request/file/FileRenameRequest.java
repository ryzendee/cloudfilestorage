package com.app.cloudfilestorage.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileRenameRequest {


    @NotBlank(message = "Updated file name must not be blank")
    private String updatedName;
    @NotBlank(message = "Current file name must not be blank")
    private String currentName;
    @NotBlank(message = "File path must not be blank")
    private String path;
    private String extension;
    private String objectName;
}

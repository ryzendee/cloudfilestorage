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
public class FileDownloadRequest {

    @NotBlank(message = "File name must not be blank")
    private String name;
    @NotBlank(message = "File path must not be blank")
    private String path;
}

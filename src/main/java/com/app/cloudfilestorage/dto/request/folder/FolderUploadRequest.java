package com.app.cloudfilestorage.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FolderUploadRequest {

    @NotBlank(message = "Current folder path must not be blank")
    private String currentFolderPath;
    @Size(min = 1, message = "Folder must contains at least 1 file")
    private List<MultipartFile> files;
}

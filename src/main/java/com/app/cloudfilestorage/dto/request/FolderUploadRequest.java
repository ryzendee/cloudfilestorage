package com.app.cloudfilestorage.dto.request;

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
    private String currentFolderPath;
    @Size(min = 1, message = "Folder must contains at least 1 file")
    private List<MultipartFile> files;
    public FolderUploadRequest(String currentFolderPath) {
        this.currentFolderPath = currentFolderPath;
    }
}

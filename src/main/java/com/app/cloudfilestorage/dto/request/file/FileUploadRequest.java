package com.app.cloudfilestorage.dto.request.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class FileUploadRequest {
    private String currentFolderPath;
    private MultipartFile file;
}

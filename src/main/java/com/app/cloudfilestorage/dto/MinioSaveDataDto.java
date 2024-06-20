package com.app.cloudfilestorage.dto;

import java.io.InputStream;

public record MinioSaveDataDto (
        String objectName,
        InputStream inputStream,
        long objectSize
) {
}

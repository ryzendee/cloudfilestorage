package com.app.cloudfilestorage.dto.response;


public record FileResponse (String name, String path, String extension, String formattedSize, String lastModified) {
}

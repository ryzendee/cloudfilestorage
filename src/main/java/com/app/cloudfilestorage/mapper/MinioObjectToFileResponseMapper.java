package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.models.MinioObject;
import org.mapstruct.*;

import java.time.ZonedDateTime;

import static com.app.cloudfilestorage.utils.DateFormatterUtil.formatZonedDateTime;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFileResponseMapper extends BaseMinioObjectMapper<FileResponse> {
    @Mapping(source = "path", target = "extension", qualifiedByName = "formatExtension")
    @Mapping(source = "path", target = "name", qualifiedByName = "formatNameFromPath")
    @Mapping(source = "path", target = "path", qualifiedByName = "removeRootUserFolderFromPath")
    @Mapping(source = "size", target = "formattedSize", qualifiedByName = "formatSize")
    @Mapping(source = "lastModified", target = "lastModified", qualifiedByName = "formatLastModified")
    FileResponse map(@Context Long userId, MinioObject from);

    @Named("formatExtension")
    default String formatExtension(String path) {
        return getExtension(path);
    }

    @Named("formatLastModified")
    default String formatLastModified(ZonedDateTime lastModified) {
        return formatZonedDateTime(lastModified);
    }

    @Named("formatSize")
    default String formatSize(long size) {
        return byteCountToDisplaySize(size);
    }
}

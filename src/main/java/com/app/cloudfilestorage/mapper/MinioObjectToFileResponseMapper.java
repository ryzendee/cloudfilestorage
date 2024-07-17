package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.models.MinioObject;
import org.mapstruct.*;

import java.time.ZonedDateTime;

import static com.app.cloudfilestorage.utils.DateFormatterUtil.formatZonedDateTime;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFileResponseMapper extends BaseMinioObjectMapper<FileResponse>{
    @Mapping(source = "path", target = "extension", qualifiedByName = "formatExtension")
    @Mapping(source = "lastModified", target = "lastModified", qualifiedByName = "formatLastModified")
    @Override
    FileResponse map(@Context Long userId, MinioObject from);

    @Named("formatExtension")
    default String formatExtension(String objectName) {
        return getExtension(objectName);
    }

    @Named("formatLastModified")
    default String formatLastModified(ZonedDateTime lastModified) {
        return formatZonedDateTime(lastModified);
    }
}

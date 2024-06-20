package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.response.FileResponse;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.utils.FileNameFormatterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MinioObjectToFileResponseMapper extends BaseMapper<MinioObject, FileResponse> {

    @Mapping(source = "path", target = "name", qualifiedByName = "formatFilenameFromPath")
    @Override
    FileResponse map(MinioObject from);

    @Named("formatFilenameFromPath")
    default String formatFilenameFromPath(String path) {
        return FileNameFormatterUtil.formatFilenameFromPath(path);
    }
}

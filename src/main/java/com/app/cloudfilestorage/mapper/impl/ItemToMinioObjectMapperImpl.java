package com.app.cloudfilestorage.mapper.impl;

import com.app.cloudfilestorage.mapper.ItemToMinioObjectMapper;
import com.app.cloudfilestorage.models.MinioObject;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ItemToMinioObjectMapperImpl implements ItemToMinioObjectMapper {

    //In minio folders do not have a modification time
    @Override
    public MinioObject map(Item from) {
        ZonedDateTime lastModified = from.isDir()
                ? null
                : from.lastModified();

        return new MinioObject(
                from.objectName(),
                lastModified,
                from.size()
        );
    }
}

package com.app.cloudfilestorage.mapper.impl;

import com.app.cloudfilestorage.mapper.ItemToMinioObjectMapper;
import com.app.cloudfilestorage.models.MinioObject;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemToMinioObjectMapperImpl implements ItemToMinioObjectMapper {

    @Override
    public MinioObject map(Item from) {
        return new MinioObject(
                from.objectName(),
                from.isDir(),
                from.size()
        );
    }
}

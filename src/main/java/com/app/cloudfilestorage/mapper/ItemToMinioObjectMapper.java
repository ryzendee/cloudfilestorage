package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.models.MinioObject;
import io.minio.messages.Item;

public interface ItemToMinioObjectMapper extends BaseMapper<Item, MinioObject> {

    MinioObject map(Item from);
}

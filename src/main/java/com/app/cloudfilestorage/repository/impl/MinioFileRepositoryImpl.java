package com.app.cloudfilestorage.repository.impl;

import com.app.cloudfilestorage.config.props.MinioProperties;
import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.ItemToMinioObjectMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioFileRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MinioFileRepositoryImpl implements MinioFileRepository {
    private static final int PART_SIZE = -1;
    private final MinioClient minioClient;
    private final ItemToMinioObjectMapper itemToMinioObjectMapper;
    private final String bucketName;

    public MinioFileRepositoryImpl(MinioClient minioClient, ItemToMinioObjectMapper itemToMinioObjectMapper, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.itemToMinioObjectMapper = itemToMinioObjectMapper;
        this.bucketName = minioProperties.bucket();
    }

    @Override
    public List<MinioObject> findAllFilesByPath(String path) {
        try {
            return findAllNonRecursive(path).stream()
                    .filter(this::isFile)
                    .map(itemToMinioObjectMapper::map)
                    .toList();
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public void saveFile(MinioSaveDataDto saveDto) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(saveDto.objectName())
                            .stream(saveDto.inputStream(), saveDto.objectSize(), PART_SIZE)
                            .build()
            );
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public void deleteFileByObjectName(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()
            );
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public InputStream downloadFileByObjectName(String path) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()
            );
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    private boolean isFile(Item item) {
        return !item.isDir();
    }

    private List<Item> findAllNonRecursive(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Item> itemList = new ArrayList<>();

        Iterable<Result<Item>> iterable = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .recursive(false)
                        .build()
        );

        for (Result<Item> itemResult : iterable) {
            Item item = itemResult.get();
            itemList.add(item);
        }

        return itemList;
    }
}

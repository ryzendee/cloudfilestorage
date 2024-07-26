package com.app.cloudfilestorage.repository;

import com.app.cloudfilestorage.config.props.MinioProperties;
import com.app.cloudfilestorage.mapper.ItemToMinioObjectMapper;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMinioRepository {

    protected final MinioClient minioClient;
    protected final ItemToMinioObjectMapper itemToMinioObjectMapper;
    protected final String bucketName;

    public AbstractMinioRepository(MinioClient minioClient, ItemToMinioObjectMapper itemToMinioObjectMapper, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.itemToMinioObjectMapper = itemToMinioObjectMapper;
        this.bucketName = minioProperties.bucket();
    }

    protected boolean isObjectNameExists(String objectName) throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        try {
            statObject(objectName);
            return true;
        } catch (ErrorResponseException ex) {
            return false;
        }
    }

    protected StatObjectResponse statObject(String objectName) throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    protected void removeObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    protected void copy(String sourceObj, String targetObj) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .source(
                                CopySource.builder()
                                        .bucket(bucketName)
                                        .object(sourceObj)
                                        .build()
                        ).bucket(bucketName)
                        .object(targetObj)
                        .build()
        );
    }

    protected List<Item> findAllNonRecursive(String path) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        return findAll(path, false);
    }

    protected List<Item> findAllRecursive(String path) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        return findAll(path, true);
    }

    private boolean isFileMissing(ErrorResponseException ex) {
        return ex.errorResponse()
                .code()
                .equals("NoSuchKey");
    }

    private List<Item> findAll(String path, boolean isRecursive) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        List<Item> itemList = new ArrayList<>();

        Iterable<Result<Item>> iterable = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .recursive(isRecursive)
                        .build()
        );

        for (Result<Item> itemResult : iterable) {
            Item item = itemResult.get();
            itemList.add(item);
        }

        return itemList;
    }

}

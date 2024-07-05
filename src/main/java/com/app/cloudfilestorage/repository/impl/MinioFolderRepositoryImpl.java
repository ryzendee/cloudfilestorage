package com.app.cloudfilestorage.repository.impl;

import com.app.cloudfilestorage.config.props.MinioProperties;
import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.ItemToMinioObjectMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioFolderRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Repository
public class MinioFolderRepositoryImpl implements MinioFolderRepository {
    private static final boolean NON_RECURSIVE = false;
    private static final boolean RECURSIVE = true;
    private static final int PART_SIZE = -1;
    private final MinioClient minioClient;
    private final ItemToMinioObjectMapper itemToMinioObjectMapper;
    private final String bucketName;

    public MinioFolderRepositoryImpl(MinioClient minioClient, ItemToMinioObjectMapper itemToMinioObjectMapper, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.itemToMinioObjectMapper = itemToMinioObjectMapper;
        this.bucketName = minioProperties.bucket();
    }

    @Override
    public void createEmptyFolder(String path) {
        try {
            if (isObjectNameExists(path)) {
                throw new MinioObjectExistsException("This object name already exists: " + path);
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, PART_SIZE)
                            .build()
            );
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public void saveAll(List<MinioSaveDataDto> files) {
        try {
            List<SnowballObject> objects = files.stream()
                    .map(this::mapToSnowBallObject)
                    .toList();

            minioClient.uploadSnowballObjects(
                    UploadSnowballObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build()
            );
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public void deleteFolderByPath(String path) {
        try {
            List<Item> itemList = findAll(path, RECURSIVE);

            for (Item item : itemList) {
                removeObject(item.objectName());
            }

        } catch (MinioException | NoSuchAlgorithmException |
                 InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public void renameFolder(String oldPath, String newPath) {
        try {
            if (isObjectNameExists(newPath)) {
                throw new MinioObjectExistsException("This object name already exists: " + newPath);
            }

            List<Item> objectsInOldFolder = findAll(oldPath, RECURSIVE);

            for (Item item : objectsInOldFolder) {
                String oldName = item.objectName();
                String newName = oldName.replaceFirst(oldPath, newPath);
                copy(oldName, newName);
                removeObject(oldName);
            }

        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public List<MinioObject> findAllFoldersByPath(String path) {
        try {
            return findAll(path, NON_RECURSIVE).stream()
                    .filter(Item::isDir)
                    .map(itemToMinioObjectMapper::map)
                    .toList();
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public ByteArrayOutputStream downloadFolderByPath(String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            List<Item> itemList = findAll(path, RECURSIVE);

            for (Item item : itemList) {
                try (InputStream objectStream = getObjectStream(item.objectName())) {
                    //Need to avoid root folders
                    String objectName = item.objectName().replaceAll(path, "");
                    zos.putNextEntry(new ZipEntry(objectName));

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = objectStream.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                }
            }

        } catch (IOException | MinioException | InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new MinioRepositoryException(ex);
        }

        return baos;
    }

    private boolean isObjectNameExists(String objectName) throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        try {
            statObject(objectName);
            return true;
        } catch (ErrorResponseException ex) {
            if (isFileMissing(ex)) {
                return false;
            }

            throw ex;
        }
    }
    private boolean isFileMissing(ErrorResponseException ex) {
        return ex.errorResponse()
                .code()
                .equals("NoSuchKey");
    }

    private StatObjectResponse statObject(String objectName) throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    private SnowballObject mapToSnowBallObject(MinioSaveDataDto minioSaveDataDto) {
        return new SnowballObject(
                minioSaveDataDto.objectName(),
                minioSaveDataDto.inputStream(),
                minioSaveDataDto.objectSize(),
                null
        );
    }

    private void copy(String sourceObj, String targetObj) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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

    private void removeObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
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

    private InputStream getObjectStream(String path) throws MinioException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(path)
                        .build()
        );
    }

}

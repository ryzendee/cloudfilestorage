package com.app.cloudfilestorage.repository.impl;

import com.app.cloudfilestorage.config.props.MinioProperties;
import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.ItemToMinioObjectMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepositoryImpl implements MinioRepository {

    private static final boolean RECURSIVE = true;
    private static final boolean NON_RECURSIVE = false;
    private static final int PART_SIZE = -1;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final ItemToMinioObjectMapper itemToMinioObjectMapper;

    @Override
    public List<MinioObject> findAll(String path) {
        try {
            List<Item> itemList = findAll(path, NON_RECURSIVE);
            List<MinioObject> minioObjectList = new ArrayList<>();

            for (Item item : itemList) {
                MinioObject minioObject = itemToMinioObjectMapper.map(item);
                minioObjectList.add(minioObject);
            }

            return minioObjectList;
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }
    @Override
    public void saveObject(MinioSaveDataDto minioSaveDataDto) throws MinioRepositoryException {
        putObject(minioSaveDataDto.objectName(), minioSaveDataDto.inputStream(), minioSaveDataDto.objectSize());
    }

    @Override
    public void createEmptyFolder(String path) {
        putObject(path, new ByteArrayInputStream(new byte[0]), 0);
    }
    @Override
    public InputStream downloadByPath(String path) {
        try {
            return getObject(path);
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    private InputStream getObject(String path) throws MinioException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(path)
                        .build()
        );
    }

    public byte[] downloadByPathAll(String path, String folderName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            List<Item> itemList = findAll(path, RECURSIVE);

            for (Item item : itemList) {
                try (InputStream objectStream = getObject(item.objectName())) {
                    //Need to avoid root folders
                    String objectName = item.objectName().replaceAll(path, folderName + "/");
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

        return baos.toByteArray();
    }



    @Override
    public void deleteAllRecursive(String path) {
        try {
            List<Item> itemList = findAll(path, RECURSIVE);

            for (Item item : itemList) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(minioProperties.bucket())
                                .object(item.objectName())
                                .build()
                );
            }
        } catch (MinioException | NoSuchAlgorithmException |
                 InvalidKeyException | IOException ex) {
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
                            .bucket(minioProperties.bucket())
                            .objects(objects)
                            .build()
            );
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    @Override
    public void renameAllRecursive(String oldPath, String newPath) {
        try {
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
    public void moveAllRecursive(String source, String target) {
        try {

            List<Item> itemList = findAll(source, RECURSIVE);

            for (Item item : itemList) {
                String oldName = item.objectName();
                String newName = target + oldName.substring(source.length());
                copy(oldName, newName);
                removeObject(oldName);
            }


        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }

    private SnowballObject mapToSnowBallObject(MinioSaveDataDto minioSaveDataDto) {
            return new SnowballObject(
                    minioSaveDataDto.objectName(),
                    minioSaveDataDto.inputStream(),
                    minioSaveDataDto.objectSize(),
                    null
            );
    }

    private List<Item> findAll(String path, boolean isRecursive) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Item> itemList = new ArrayList<>();

        Iterable<Result<Item>> iterable = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.bucket())
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

    private void copy(String sourceObj, String targetObj) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .source(
                                CopySource.builder()
                                        .bucket(minioProperties.bucket())
                                        .object(sourceObj)
                                        .build()
                        ).bucket(minioProperties.bucket())
                        .object(targetObj)
                        .build()
        );
    }

    private void removeObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(objectName)
                        .build()
        );
    }

    private void putObject(String objectName, InputStream inputStream, long size) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucket())
                            .object(objectName)
                            .stream(inputStream, size, PART_SIZE)
                            .build()
            );
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }
}

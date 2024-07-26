package com.app.cloudfilestorage.repository.impl;

import com.app.cloudfilestorage.config.props.MinioProperties;
import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.mapper.ItemToMinioObjectMapper;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.AbstractMinioRepository;
import com.app.cloudfilestorage.repository.MinioFileRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Repository
public class MinioFileRepositoryImpl extends AbstractMinioRepository implements MinioFileRepository {
    private static final int PART_SIZE = -1;

    public MinioFileRepositoryImpl(MinioClient minioClient, ItemToMinioObjectMapper itemToMinioObjectMapper, MinioProperties minioProperties) {
        super(minioClient, itemToMinioObjectMapper, minioProperties);
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
    public List<MinioObject> findAllFilesByPathRecursive(String path) {
        try {
            return findAllRecursive(path).stream()
                    .filter(this::isFile)
                    .map(itemToMinioObjectMapper::map)
                    .toList();
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }    }

    //TODO: Add an increment fileName instead of creating an exception
    @Override
    public void saveFile(MinioSaveDataDto saveDto) {
        try {
            if (isObjectNameExists(saveDto.objectName())) {
                throw new MinioObjectExistsException("This object name already exists: " + saveDto.objectName());
            }

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

    @Override
    public void renameFile(String currentName, String updatedName) {
        try {
            if (isObjectNameExists(updatedName)) {
                throw new MinioObjectExistsException(updatedName + " is already eists!");
            }

            copy(currentName, updatedName);
            removeObject(currentName);
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            throw new MinioRepositoryException(ex);
        }
    }


    private boolean isFile(Item item) {
        return !item.isDir();
    }

}

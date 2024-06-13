package com.app.cloudfilestorage.config;

import com.app.cloudfilestorage.config.props.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@RequiredArgsConstructor
public class MinioBucketInitializer {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;


    @PostConstruct
    public void initializeBucket() {
        try {
            String bucketName = minioProperties.bucket();

            if (!isBucketExists(bucketName)) {
                createBucket(bucketName);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isBucketExists(String bucketName) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        return minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
    }

    private void createBucket(String bucketName) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
    }
}

package com.app.cloudfilestorage.IT;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest
public abstract class ITBase {

    private static final String POSTGRES_IMAGE = "postgres:15.6";

    private static final String MINIO_IMAGE = "minio/minio:RELEASE.2024-02-17T01-15-57Z.fips";
    private static final String MINIO_ENDPOINT = "minio.endpoint";
    private static final String MINIO_USERNAME = "minio.username";
    private static final String MINIO_PASSWORD = "minio.password";

    @ServiceConnection
    static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE);
    static final MinIOContainer minioContainer = new MinIOContainer(MINIO_IMAGE);

    static {
        postgreSQLContainer.start();
        minioContainer.start();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add(MINIO_ENDPOINT, minioContainer::getS3URL);
        registry.add(MINIO_USERNAME, minioContainer::getUserName);
        registry.add(MINIO_PASSWORD, minioContainer::getPassword);
    }
}

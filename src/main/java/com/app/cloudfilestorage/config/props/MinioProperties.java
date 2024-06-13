package com.app.cloudfilestorage.config.props;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties (
        String endpoint,
        String username,
        String password,
        String bucket
) {

}
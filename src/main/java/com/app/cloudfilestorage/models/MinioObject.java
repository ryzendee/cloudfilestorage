package com.app.cloudfilestorage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinioObject {
    private String path;
    private ZonedDateTime lastModified;
    private long size;
}

package com.app.cloudfilestorage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinioObject {
    private String path;
    private boolean isDir;
    private long size;
}

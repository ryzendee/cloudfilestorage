package com.app.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class BreadcrumbDto {

    private final List<String> folderNamesList;
    private final List<String> folderPathsList;
}

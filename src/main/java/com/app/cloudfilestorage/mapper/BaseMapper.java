package com.app.cloudfilestorage.mapper;

public interface BaseMapper <F, T> {

    T map (F from);
}

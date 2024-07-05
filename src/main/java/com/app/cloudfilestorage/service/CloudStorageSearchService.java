package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.response.SearchResultResponse;


public interface CloudStorageSearchService {

    SearchResultResponse searchByQuery(Long userId, String query);
}

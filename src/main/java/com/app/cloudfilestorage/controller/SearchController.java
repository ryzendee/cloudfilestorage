package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.dto.request.*;
import com.app.cloudfilestorage.dto.response.SearchResultResponse;
import com.app.cloudfilestorage.service.CloudStorageSearchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final CloudStorageSearchService cloudStorageSearchService;

    @GetMapping
    public String search(@RequestParam String query,
                         @SessionAttribute UserSessionDto userSessionDto,
                         Model model) {
        if (!isQueryValid(query)) {
            model.addAttribute("validationErrorMessage", "Search query must not be blank or empty");
        } else {
            SearchResultResponse searchResult = cloudStorageSearchService.searchByQuery(userSessionDto.id(), query);
            model.addAttribute("searchResult", searchResult);

            model.addAttribute("folderDeleteRequest", new FolderDeleteRequest());
            model.addAttribute("folderDownloadRequest", new FolderDownloadRequest());
            model.addAttribute("folderRenameRequest", new FolderRenameRequest());

            model.addAttribute("fileDownloadRequest", new FileDownloadRequest());
            model.addAttribute("fileDeleteRequest", new FileDeleteRequest());
        }

        return "search-view";
    }

    private boolean isQueryValid(String query) {
        return !StringUtils.isBlank(query);
    }
}

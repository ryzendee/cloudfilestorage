package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.request.file.FileDeleteRequest;
import com.app.cloudfilestorage.dto.request.file.FileDownloadRequest;
import com.app.cloudfilestorage.dto.request.folder.FolderDeleteRequest;
import com.app.cloudfilestorage.dto.request.folder.FolderDownloadRequest;
import com.app.cloudfilestorage.dto.request.folder.FolderRenameRequest;
import com.app.cloudfilestorage.dto.response.SearchResultResponse;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.enums.FlashAttr;
import com.app.cloudfilestorage.service.CloudStorageSearchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private static final String HOME_PAGE = "/";
    private static final String FLASH_ATR_VALIDATION_ERROR_MESSAGE = FlashAttr.VALIDATION_ERROR_MESSAGE.getName();

    private final CloudStorageSearchService cloudStorageSearchService;

    @GetMapping
    public Object search(@RequestParam String query,
                         @AuthenticationPrincipal UserEntity currentUser,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (!isQueryValid(query)) {
            redirectAttributes.addFlashAttribute(FLASH_ATR_VALIDATION_ERROR_MESSAGE, "Search query must not be blank or empty");
            return new RedirectView(HOME_PAGE);
        } else {
            SearchResultResponse searchResult = cloudStorageSearchService.searchByQuery(currentUser.getId(), query);
            model.addAttribute("searchResult", searchResult);

            model.addAllAttributes(getFolderRequestAttributeMap());
            model.addAllAttributes(getFileRequestAttributeMap());
        }

        return "search-view";
    }

    private boolean isQueryValid(String query) {
        return !StringUtils.isBlank(query);
    }

    private Map<String, Object> getFolderRequestAttributeMap() {
        return Map.of(
                "folderDeleteRequest", new FolderDeleteRequest(),
                "folderDownloadRequest", new FolderDownloadRequest(),
                "folderRenameRequest", new FolderRenameRequest()
                );
    }

    private Map<String, Object> getFileRequestAttributeMap() {
        return Map.of(
                "fileDownloadRequest", new FileDownloadRequest(),
                "fileDeleteRequest", new FileDeleteRequest()
        );
    }
}

package com.app.cloudfilestorage.unit.controller;

import com.app.cloudfilestorage.config.TestSecurityConfig;
import com.app.cloudfilestorage.controller.SearchController;
import com.app.cloudfilestorage.dto.response.SearchResultResponse;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.enums.FlashAttr;
import com.app.cloudfilestorage.service.CloudStorageSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {

    private static final String HOME_PAGE_URI = "/";
    private static final String BASE_PATH = "/search";
    private static final String FLASH_ATR_VALIDATION_ERROR_MESSAGE = FlashAttr.VALIDATION_ERROR_MESSAGE.getName();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CloudStorageSearchService cloudStorageSearchService;

    @DisplayName("Search: should return view with status ok")
    @Test
    void search_validQuery_shouldReturnViewWithStatusOk() throws Exception {
        String query = "my-files";
        UserEntity userEntity = getUserEntity();
        SearchResultResponse searchResultResponse = getSearchResultResponse();

        when(cloudStorageSearchService.searchByQuery(userEntity.getId(), query))
                .thenReturn(searchResultResponse);

        mockMvc.perform(
                get(BASE_PATH)
                        .param("query", query)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().isOk(),
                view().name("search-view")
        );

        verify(cloudStorageSearchService).searchByQuery(userEntity.getId(), query);
    }

    @DisplayName("Search: should redirect to home with validationErrorMessage flash attribute")
    @EmptySource
    @ParameterizedTest
    void search_queryIsEmpty_shouldRedirectToHomeWithValidationErrorMessage(String invalidQuery) throws Exception {
        UserEntity userEntity = getUserEntity();

        mockMvc.perform(
                get(BASE_PATH)
                        .param("query", invalidQuery)
                        .with(csrf())
                        .with(user(userEntity))
        ).andExpectAll(
                status().is3xxRedirection(),
                flash().attributeExists(FLASH_ATR_VALIDATION_ERROR_MESSAGE),
                redirectedUrl(HOME_PAGE_URI)
        );

        verify(cloudStorageSearchService, never()).searchByQuery(userEntity.getId(), invalidQuery);
    }

    private UserEntity getUserEntity() {
        return new UserEntity(1L, "test-username", "test-password");
    }

    private SearchResultResponse getSearchResultResponse() {
        return new SearchResultResponse(
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}

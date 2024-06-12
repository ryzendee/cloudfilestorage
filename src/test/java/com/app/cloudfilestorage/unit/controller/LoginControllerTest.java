package com.app.cloudfilestorage.unit.controller;

import com.app.cloudfilestorage.config.TestSecurityConfig;
import com.app.cloudfilestorage.controller.LoginController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(LoginController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Get login view")
    @Test
    void getLoginView_existsView_returnsView() throws Exception {
        mockMvc.perform(
                get("/login")
        ).andExpectAll(
                status().isOk(),
                view().name("login-view")
        );
    }
}

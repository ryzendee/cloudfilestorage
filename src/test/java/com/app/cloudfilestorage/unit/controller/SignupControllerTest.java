package com.app.cloudfilestorage.unit.controller;


import com.app.cloudfilestorage.config.TestSecurityConfig;
import com.app.cloudfilestorage.controller.SignupController;
import com.app.cloudfilestorage.dto.request.SignupRequest;
import com.app.cloudfilestorage.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignupController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class SignupControllerTest {

    private static final String VALID_USERNAME = "username";
    private static final String VALID_PASSWORD = "password";
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @DisplayName("Get signup view")
    @Test
    void getSignupView_viewExists_returnsView() throws Exception {
        mockMvc.perform(
                get("/signup")
                        .with(csrf())
        ).andExpectAll(
                status().isOk(),
                model().attributeExists("signupRequest"),
                view().name("signup-view")
        );
    }

    @DisplayName("Successfully signup")
    @Test
    void signupUser_validRequest_savesUser() throws Exception {
        SignupRequest validRequest = new SignupRequest(VALID_USERNAME, VALID_PASSWORD, VALID_PASSWORD);

        mockMvc.perform(
                post("/signup")
                        .flashAttr("signupRequest", validRequest)
                        .with(csrf())
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl("/login"),
                flash().attributeExists("successMessage")
        );

        verify(userService).createUser(validRequest);
    }

    @DisplayName("Username is invalid: parametrized")
    @MethodSource("getArgsForInvalidUsername")
    @ParameterizedTest
    void signupUser_usernameIsInvalid_redirectsToSignup(String username) throws Exception {
        SignupRequest invalidRequest = new SignupRequest(username, VALID_PASSWORD, VALID_PASSWORD);

        mockMvc.perform(
                post("/signup")
                        .flashAttr("signupRequest", invalidRequest)
                        .with(csrf())
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl("/signup"),
                flash().attributeExists("validationErrorMessage")
        );

        verify(userService, never()).createUser(invalidRequest);
    }

    static Stream<Arguments> getArgsForInvalidUsername() {
        return Stream.of(
                arguments(named("Username is empty", "  ")),
                arguments(named("Username is null", null)),
                arguments(named("Username is short", "usr"))
        );
    }

    @DisplayName("Passwords do not match")
    @Test
    void signupUser_passwordsDoNotMatch_redirectsToSignup() throws Exception {
        SignupRequest invalidRequest = new SignupRequest(VALID_USERNAME, "password", "passwordConfirmation");

        mockMvc.perform(
                post("/signup")
                        .flashAttr("signupRequest", invalidRequest)
                        .with(csrf())
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl("/signup"),
                flash().attributeExists("validationErrorMessage")
        );

        verify(userService, never()).createUser(invalidRequest);
    }

    @DisplayName("Passwords are invalid: parametrized")
    @MethodSource("getArgsForInvalidPasswords")
    @ParameterizedTest(name = "{index} : {0}")
    void signupUser_passwordsAreInvalid_redirectsToSignup(String password) throws Exception {
        SignupRequest invalidRequest = new SignupRequest(VALID_USERNAME, password, password);

        mockMvc.perform(
                post("/signup")
                        .flashAttr("signupRequest", invalidRequest)
                        .with(csrf())
        ).andExpectAll(
                status().is3xxRedirection(),
                redirectedUrl("/signup"),
                flash().attributeExists("validationErrorMessage")
        );

        verify(userService, never()).createUser(invalidRequest);
    }

    static Stream<Arguments> getArgsForInvalidPasswords() {
        return Stream.of(
                arguments(named("Passwords are empty", "  ")),
                arguments(named("Passwords are null", null)),
                arguments(named("Passwords are short", "psw"))
        );
    }
}

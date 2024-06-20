package com.app.cloudfilestorage.security;

import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.service.UserSessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    private static final String USER_SESSION_DTO_ATR = "userSessionDto";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private final UserSessionService userSessionService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserSessionDto userSessionDto = userSessionService.getUserSessionDtoByUsername(authentication.getName());

        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(USER_SESSION_DTO_ATR, userSessionDto);

        response.sendRedirect(DEFAULT_SUCCESS_URL);
    }
}


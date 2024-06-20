package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.UserSessionDto;


public interface UserSessionService {

    UserSessionDto getUserSessionDtoByUsername(String username);
}

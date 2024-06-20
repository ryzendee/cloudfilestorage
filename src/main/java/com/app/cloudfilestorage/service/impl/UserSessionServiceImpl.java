package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.mapper.UserEntityToUserSessionDtoMapper;
import com.app.cloudfilestorage.service.UserService;
import com.app.cloudfilestorage.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserService userService;
    private final UserEntityToUserSessionDtoMapper userSessionMapper;

    @Override
    public UserSessionDto getUserSessionDtoByUsername(String username) {
        UserEntity userEntity =  userService.getUserByUsername(username);

        return userSessionMapper.map(userEntity);
    }
}

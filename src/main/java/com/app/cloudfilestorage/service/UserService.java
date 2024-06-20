package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.request.SignupRequest;
import com.app.cloudfilestorage.entity.UserEntity;

public interface UserService {

    void createUser(SignupRequest dtoRequest);
    UserEntity getUserByUsername(String username);
}

package com.app.cloudfilestorage.service;

import com.app.cloudfilestorage.dto.request.SignupRequest;

public interface UserService {

    void createUser(SignupRequest dtoRequest);
}

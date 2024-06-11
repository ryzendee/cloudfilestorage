package com.app.cloudfilestorage.service.impl;

import com.app.cloudfilestorage.dto.request.SignupRequest;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.exception.SignupException;
import com.app.cloudfilestorage.mapper.SignUpToUserMapper;
import com.app.cloudfilestorage.repository.UserRepository;
import com.app.cloudfilestorage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SignUpToUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(SignupRequest signupRequest) throws SignupException {
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        UserEntity user = userMapper.map(signupRequest);
        user.setPassword(encodedPassword);
        saveUser(user);
        log.info("User was saved: {}", user);
    }

    private void saveUser(UserEntity user) throws SignupException {
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Failed to save user: {}", user.getUsername(), ex);
            throw new SignupException("Failed to save user, username already exists");
        }
    }
}

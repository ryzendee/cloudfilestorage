package com.app.cloudfilestorage.IT;

import com.app.cloudfilestorage.dto.request.SignupRequest;
import com.app.cloudfilestorage.entity.UserEntity;
import com.app.cloudfilestorage.exception.SignupException;
import com.app.cloudfilestorage.repository.UserRepository;
import com.app.cloudfilestorage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public class UserServiceIT {

    private static final String POSTGRES_IMAGE = "postgres:15.6";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE);

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_existsUsername_throwSignupException() {
        //given
        UserEntity user = new UserEntity("username", "password");
        userRepository.save(user);
        SignupRequest signupRequest = new SignupRequest(user.getUsername(), user.getPassword(), user.getPassword());

        //when & then
        assertThatThrownBy(() -> userService.createUser(signupRequest))
                .isInstanceOf(SignupException.class);
    }

    @Test
    void createUser_nonExistsUsername_savesUserInDatabase() {
        //given
        SignupRequest signupRequest = new SignupRequest("username", "password", "password");

        //when
        userService.createUser(signupRequest);

        //then
        UserEntity userEntity = userRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getUsername()).isEqualTo(signupRequest.getUsername());
        assertThat(userEntity.getPassword()).isNotBlank();
    }
}


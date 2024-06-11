package com.app.cloudfilestorage.dto.request;

import com.app.cloudfilestorage.validation.PasswordMatcher;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@PasswordMatcher
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @Size(min = 5, message = "Password must be at least 5 symbols length")
    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 5, message = "Password must be at least 5 symbols length")
    @NotBlank(message = "Password is required")
    private String password;

    private String passwordConfimation;
}

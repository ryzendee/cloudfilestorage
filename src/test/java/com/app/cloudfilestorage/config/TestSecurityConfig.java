package com.app.cloudfilestorage.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .formLogin(formConfigurer -> formConfigurer
                        .loginPage("/login")
                        .permitAll()
                ).logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/logout")
                ).authorizeHttpRequests(requestMatcher -> requestMatcher
                        .requestMatchers(
                                "/signup",
                                "/login",
                                "/logout",
                                "/access-denied").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handlingConfigurer ->
                        handlingConfigurer.accessDeniedPage("/access-denied")
                );

        return httpSecurity.build();
    }
}

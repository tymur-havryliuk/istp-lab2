package com.istp.api.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.istp.api.common.UserRole;
import com.istp.api.dao.persistence.entity.UserEntity;
import com.istp.api.dao.persistence.mapper.UserPersistenceMapper;
import com.istp.api.dao.persistence.repository.UserRepository;
import com.istp.api.exception.UnauthorizedException;
import com.istp.api.security.JwtService;
import com.istp.api.service.model.AuthenticationResult;
import com.istp.api.service.model.LoginCredentials;
import com.istp.api.service.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    private static final String EMAIL = "ivan.petrenko@example.com";
    private static final String PASSWORD = "password123";
    private static final String HASH = "$2a$10$hash";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPersistenceMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginSucceedsForActiveUserWithValidPassword() {
        UserEntity entity = activeUserEntity();
        User user = activeUser(true);
        LoginCredentials credentials = credentials();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(entity));
        when(userMapper.toModel(entity)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResult result = authService.login(credentials);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void loginFailsForWrongPassword() {
        UserEntity entity = activeUserEntity();
        LoginCredentials credentials = credentials();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(entity));
        when(userMapper.toModel(entity)).thenReturn(activeUser(true));
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(credentials))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void loginFailsForInactiveUser() {
        UserEntity entity = activeUserEntity();
        LoginCredentials credentials = credentials();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(entity));
        when(userMapper.toModel(entity)).thenReturn(activeUser(false));

        assertThatThrownBy(() -> authService.login(credentials))
                .isInstanceOf(UnauthorizedException.class);
        verify(passwordEncoder, never()).matches(PASSWORD, HASH);
    }

    private LoginCredentials credentials() {
        return LoginCredentials.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    private UserEntity activeUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setEmail(EMAIL);
        entity.setPasswordHash(HASH);
        entity.setActive(true);
        entity.setRole(UserRole.USER);
        return entity;
    }

    private User activeUser(boolean active) {
        return User.builder()
                .id(1L)
                .email(EMAIL)
                .passwordHash(HASH)
                .role(UserRole.USER)
                .active(active)
                .build();
    }
}

package com.istp.api.service.impl;

import com.istp.api.common.UserRole;
import com.istp.api.dao.persistence.mapper.UserPersistenceMapper;
import com.istp.api.dao.persistence.repository.UserRepository;
import com.istp.api.exception.BusinessRuleException;
import com.istp.api.exception.UnauthorizedException;
import com.istp.api.security.JwtService;
import com.istp.api.service.api.AuthService;
import com.istp.api.service.model.AuthenticationResult;
import com.istp.api.service.model.LoginCredentials;
import com.istp.api.service.model.RegisterUserCommand;
import com.istp.api.service.model.TestJwtCommand;
import com.istp.api.service.model.User;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserPersistenceMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResult login(LoginCredentials credentials) {
        User user = userRepository.findByEmail(credentials.getEmail())
                .map(userMapper::toModel)
                .filter(User::isActive)
                .filter(found -> passwordEncoder.matches(credentials.getPassword(), found.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        return AuthenticationResult.builder()
                .token(jwtService.generateToken(user))
                .user(user)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResult register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessRuleException("User with this email already exists");
        }

        User user = User.builder()
                .fullName(command.getFullName())
                .email(command.getEmail())
                .passwordHash(passwordEncoder.encode(command.getPassword()))
                .role(UserRole.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userMapper.toModel(userRepository.save(userMapper.toEntity(user)));

        return AuthenticationResult.builder()
                .token(jwtService.generateToken(savedUser))
                .user(savedUser)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResult createTestToken(TestJwtCommand command) {
        String email = command.getEmail() == null || command.getEmail().isBlank()
                ? "test-" + command.getRole().name().toLowerCase() + "@example.com"
                : command.getEmail();
        User user = userRepository.findByEmail(email)
                .map(userMapper::toModel)
                .orElseGet(() -> createTestUser(email, command.getRole()));

        return AuthenticationResult.builder()
                .token(jwtService.generateTestToken(user))
                .user(user)
                .build();
    }

    private User createTestUser(String email, UserRole role) {
        User user = User.builder()
                .fullName("Test " + role.name())
                .email(email)
                .passwordHash(passwordEncoder.encode("test-password"))
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        return userMapper.toModel(userRepository.save(userMapper.toEntity(user)));
    }
}

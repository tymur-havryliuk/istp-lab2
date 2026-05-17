package com.istp.api.service.impl;

import com.istp.api.dao.persistence.mapper.UserPersistenceMapper;
import com.istp.api.dao.persistence.repository.UserRepository;
import com.istp.api.exception.UnauthorizedException;
import com.istp.api.security.JwtService;
import com.istp.api.service.api.AuthService;
import com.istp.api.service.model.AuthenticationResult;
import com.istp.api.service.model.LoginCredentials;
import com.istp.api.service.model.User;
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
}

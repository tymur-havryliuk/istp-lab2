package com.istp.api.controller;

import com.istp.api.dto.request.LoginRequest;
import com.istp.api.dto.response.LoginResponse;
import com.istp.api.service.api.AuthService;
import com.istp.api.service.mapper.AuthDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthDtoMapper authDtoMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authDtoMapper.toResponse(authService.login(authDtoMapper.toModel(request))));
    }
}

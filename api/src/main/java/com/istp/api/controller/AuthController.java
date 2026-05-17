package com.istp.api.controller;

import com.istp.api.dto.request.LoginRequest;
import com.istp.api.dto.request.RegisterRequest;
import com.istp.api.dto.request.TestJwtRequest;
import com.istp.api.dto.response.LoginResponse;
import com.istp.api.service.api.AuthService;
import com.istp.api.service.mapper.AuthDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;
    private final AuthDtoMapper authDtoMapper;

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authDtoMapper.toResponse(authService.login(authDtoMapper.toModel(request))));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new USER account and receive JWT")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authDtoMapper.toResponse(authService.register(authDtoMapper.toModel(request)));
        return ResponseEntity.created(URI.create("/api/users/" + response.getUser().getId())).body(response);
    }

    @PostMapping("/test-token")
    @Operation(
            summary = "[TEST ONLY] Generate JWT with selected role",
            description = "Public helper endpoint for manual Swagger/API testing. "
                    + "Creates or reuses a database-backed test user for the requested email and role, "
                    + "then returns a JWT with a testToken flag. The returned token can authenticate as USER "
                    + "or TECHNICIAN and can be used in endpoints that persist user ids."
    )
    public ResponseEntity<LoginResponse> createTestToken(@Valid @RequestBody TestJwtRequest request) {
        return ResponseEntity.ok(authDtoMapper.toResponse(authService.createTestToken(authDtoMapper.toModel(request))));
    }
}

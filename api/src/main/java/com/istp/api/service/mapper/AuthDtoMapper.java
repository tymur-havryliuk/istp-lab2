package com.istp.api.service.mapper;

import com.istp.api.dto.request.LoginRequest;
import com.istp.api.dto.request.RegisterRequest;
import com.istp.api.dto.request.TestJwtRequest;
import com.istp.api.dto.response.LoginResponse;
import com.istp.api.dto.response.UserResponse;
import com.istp.api.service.model.AuthenticationResult;
import com.istp.api.service.model.LoginCredentials;
import com.istp.api.service.model.RegisterUserCommand;
import com.istp.api.service.model.TestJwtCommand;
import com.istp.api.service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthDtoMapper {
    LoginCredentials toModel(LoginRequest request);

    RegisterUserCommand toModel(RegisterRequest request);

    TestJwtCommand toModel(TestJwtRequest request);

    UserResponse toResponse(User user);

    LoginResponse toResponse(AuthenticationResult result);
}

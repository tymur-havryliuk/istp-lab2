package com.istp.api.service.api;

import com.istp.api.service.model.AuthenticationResult;
import com.istp.api.service.model.LoginCredentials;
import com.istp.api.service.model.RegisterUserCommand;
import com.istp.api.service.model.TestJwtCommand;

public interface AuthService {
    AuthenticationResult login(LoginCredentials credentials);

    AuthenticationResult register(RegisterUserCommand command);

    AuthenticationResult createTestToken(TestJwtCommand command);
}

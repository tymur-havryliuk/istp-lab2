package com.istp.api.service.api;

import com.istp.api.service.model.AuthenticationResult;
import com.istp.api.service.model.LoginCredentials;

public interface AuthService {
    AuthenticationResult login(LoginCredentials credentials);
}

package com.istp.api.security;

import com.istp.api.common.UserRole;

public interface AuthenticatedUser {
    Long id();

    String email();

    UserRole role();
}

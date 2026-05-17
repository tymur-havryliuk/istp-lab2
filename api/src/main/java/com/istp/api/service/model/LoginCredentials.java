package com.istp.api.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCredentials {
    private String email;
    private String password;
}

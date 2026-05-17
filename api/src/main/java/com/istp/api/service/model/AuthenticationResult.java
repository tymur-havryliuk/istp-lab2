package com.istp.api.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResult {
    private String token;
    private User user;
}

package com.istp.api.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserCommand {
    private String fullName;
    private String email;
    private String password;
}

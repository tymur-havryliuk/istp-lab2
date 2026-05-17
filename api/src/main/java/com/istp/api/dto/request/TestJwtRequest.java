package com.istp.api.dto.request;

import com.istp.api.common.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestJwtRequest {
    private Long userId;

    @Email
    private String email = "test@example.com";

    @NotNull
    private UserRole role;
}

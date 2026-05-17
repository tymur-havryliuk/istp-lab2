package com.istp.api.service.model;

import com.istp.api.common.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestJwtCommand {
    private Long userId;
    private String email;
    private UserRole role;
}

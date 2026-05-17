package com.istp.api.service.model;

import com.istp.api.common.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String fullName;
    private String email;
    private String passwordHash;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
}

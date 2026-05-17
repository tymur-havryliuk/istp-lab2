package com.istp.api.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechnicianCommentResponse {
    private Long id;
    private String technicianComment;
    private LocalDateTime updatedAt;
}

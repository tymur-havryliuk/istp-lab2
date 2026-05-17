package com.istp.api.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCommentResponse {
    private Long id;
    private String userComment;
    private LocalDateTime updatedAt;
}

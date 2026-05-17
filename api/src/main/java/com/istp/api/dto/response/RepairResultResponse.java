package com.istp.api.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairResultResponse {
    private Long id;
    private String repairResult;
    private LocalDateTime updatedAt;
}

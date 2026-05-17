package com.istp.api.dto.response;

import com.istp.api.common.RequestStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusUpdateResponse {
    private Long id;
    private RequestStatus status;
    private LocalDateTime updatedAt;
}

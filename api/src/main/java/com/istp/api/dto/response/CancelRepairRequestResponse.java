package com.istp.api.dto.response;

import com.istp.api.common.RequestStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelRepairRequestResponse {
    private Long id;
    private RequestStatus status;
    private LocalDateTime cancelledAt;
    private LocalDateTime updatedAt;
}

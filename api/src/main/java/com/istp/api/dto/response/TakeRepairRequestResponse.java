package com.istp.api.dto.response;

import com.istp.api.common.RequestStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TakeRepairRequestResponse {
    private Long id;
    private Long technicianId;
    private RequestStatus status;
    private LocalDateTime updatedAt;
}

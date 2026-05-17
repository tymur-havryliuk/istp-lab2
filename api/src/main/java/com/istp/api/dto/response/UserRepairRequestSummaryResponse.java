package com.istp.api.dto.response;

import com.istp.api.common.EquipmentType;
import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRepairRequestSummaryResponse {
    private Long id;
    private EquipmentType equipmentType;
    private String equipmentName;
    private String title;
    private RequestStatus status;
    private RequestPriority priority;
    private LocalDateTime createdAt;
}

package com.istp.api.service.model;

import com.istp.api.common.EquipmentType;
import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairRequest {
    private Long id;
    private Long userId;
    private Long technicianId;
    private EquipmentType equipmentType;
    private String equipmentName;
    private String title;
    private String problemDescription;
    private String userComment;
    private String technicianComment;
    private String repairResult;
    private RequestStatus status;
    private RequestPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime closedAt;
}

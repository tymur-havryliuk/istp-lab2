package com.istp.api.dto.request;

import com.istp.api.common.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TechnicianStatusUpdateRequest {
    @NotNull
    private RequestStatus status;
}

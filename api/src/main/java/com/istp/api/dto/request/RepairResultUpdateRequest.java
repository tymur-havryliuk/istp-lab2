package com.istp.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RepairResultUpdateRequest {
    @NotBlank
    private String repairResult;
}

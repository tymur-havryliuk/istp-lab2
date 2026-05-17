package com.istp.api.dto.request;

import com.istp.api.common.EquipmentType;
import com.istp.api.common.RequestPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRepairRequest {
    @NotNull
    private EquipmentType equipmentType;

    @Size(max = 150)
    private String equipmentName;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String problemDescription;

    private String userComment;

    @NotNull
    private RequestPriority priority;
}

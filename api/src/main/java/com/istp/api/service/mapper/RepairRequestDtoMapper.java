package com.istp.api.service.mapper;

import com.istp.api.dto.request.CreateRepairRequest;
import com.istp.api.dto.response.CancelRepairRequestResponse;
import com.istp.api.dto.response.CloseRepairRequestResponse;
import com.istp.api.dto.response.RepairRequestDetailsResponse;
import com.istp.api.dto.response.RepairRequestSummaryResponse;
import com.istp.api.dto.response.RepairResultResponse;
import com.istp.api.dto.response.StatusUpdateResponse;
import com.istp.api.dto.response.TakeRepairRequestResponse;
import com.istp.api.dto.response.TechnicianCommentResponse;
import com.istp.api.dto.response.UserRepairRequestSummaryResponse;
import com.istp.api.dto.response.UserCommentResponse;
import com.istp.api.service.model.RepairRequest;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepairRequestDtoMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "equipmentType", source = "equipmentType")
    @Mapping(target = "equipmentName", source = "equipmentName")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "problemDescription", source = "problemDescription")
    @Mapping(target = "userComment", source = "userComment")
    @Mapping(target = "priority", source = "priority")
    RepairRequest toModel(CreateRepairRequest request);

    RepairRequestDetailsResponse toDetailsResponse(RepairRequest request);

    RepairRequestSummaryResponse toSummaryResponse(RepairRequest request);

    List<RepairRequestSummaryResponse> toSummaryResponses(List<RepairRequest> requests);

    UserRepairRequestSummaryResponse toUserSummaryResponse(RepairRequest request);

    List<UserRepairRequestSummaryResponse> toUserSummaryResponses(List<RepairRequest> requests);

    UserCommentResponse toUserCommentResponse(RepairRequest request);

    CancelRepairRequestResponse toCancelResponse(RepairRequest request);

    TakeRepairRequestResponse toTakeResponse(RepairRequest request);

    StatusUpdateResponse toStatusResponse(RepairRequest request);

    TechnicianCommentResponse toTechnicianCommentResponse(RepairRequest request);

    RepairResultResponse toRepairResultResponse(RepairRequest request);

    CloseRepairRequestResponse toCloseResponse(RepairRequest request);
}

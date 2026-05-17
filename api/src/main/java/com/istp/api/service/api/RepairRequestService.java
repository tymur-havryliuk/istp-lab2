package com.istp.api.service.api;

import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import com.istp.api.service.model.RepairRequest;
import java.util.List;

public interface RepairRequestService {
    List<RepairRequest> getMyRequests();

    RepairRequest create(RepairRequest request);

    RepairRequest getDetails(Long id);

    RepairRequest updateUserComment(Long id, String userComment);

    RepairRequest cancel(Long id);

    List<RepairRequest> getTechnicianRequests(RequestStatus status, RequestPriority priority);

    RepairRequest take(Long id);

    RepairRequest updateTechnicianStatus(Long id, RequestStatus status);

    RepairRequest updateTechnicianComment(Long id, String technicianComment);

    RepairRequest updateRepairResult(Long id, String repairResult);

    RepairRequest close(Long id, String repairResult);
}

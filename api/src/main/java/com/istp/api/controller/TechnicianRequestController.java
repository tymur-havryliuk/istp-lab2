package com.istp.api.controller;

import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import com.istp.api.dto.request.RepairResultUpdateRequest;
import com.istp.api.dto.request.TechnicianCommentUpdateRequest;
import com.istp.api.dto.request.TechnicianStatusUpdateRequest;
import com.istp.api.dto.response.CloseRepairRequestResponse;
import com.istp.api.dto.response.RepairRequestSummaryResponse;
import com.istp.api.dto.response.RepairResultResponse;
import com.istp.api.dto.response.StatusUpdateResponse;
import com.istp.api.dto.response.TakeRepairRequestResponse;
import com.istp.api.dto.response.TechnicianCommentResponse;
import com.istp.api.service.api.RepairRequestService;
import com.istp.api.service.mapper.RepairRequestDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/technician/requests")
@PreAuthorize("hasRole('TECHNICIAN')")
@RequiredArgsConstructor
public class TechnicianRequestController {
    private final RepairRequestService repairRequestService;
    private final RepairRequestDtoMapper repairRequestDtoMapper;

    @GetMapping
    public ResponseEntity<List<RepairRequestSummaryResponse>> getRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestPriority priority
    ) {
        return ResponseEntity.ok(
                repairRequestDtoMapper.toSummaryResponses(repairRequestService.getTechnicianRequests(status, priority))
        );
    }

    @PatchMapping("/{id}/take")
    public ResponseEntity<TakeRepairRequestResponse> take(@PathVariable Long id) {
        return ResponseEntity.ok(repairRequestDtoMapper.toTakeResponse(repairRequestService.take(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StatusUpdateResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TechnicianStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                repairRequestDtoMapper.toStatusResponse(
                        repairRequestService.updateTechnicianStatus(id, request.getStatus())
                )
        );
    }

    @PatchMapping("/{id}/technician-comment")
    public ResponseEntity<TechnicianCommentResponse> updateTechnicianComment(
            @PathVariable Long id,
            @RequestBody TechnicianCommentUpdateRequest request
    ) {
        return ResponseEntity.ok(
                repairRequestDtoMapper.toTechnicianCommentResponse(
                        repairRequestService.updateTechnicianComment(id, request.getTechnicianComment())
                )
        );
    }

    @PatchMapping("/{id}/repair-result")
    public ResponseEntity<RepairResultResponse> updateRepairResult(
            @PathVariable Long id,
            @Valid @RequestBody RepairResultUpdateRequest request
    ) {
        return ResponseEntity.ok(
                repairRequestDtoMapper.toRepairResultResponse(
                        repairRequestService.updateRepairResult(id, request.getRepairResult())
                )
        );
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<CloseRepairRequestResponse> close(
            @PathVariable Long id,
            @Valid @RequestBody RepairResultUpdateRequest request
    ) {
        return ResponseEntity.ok(
                repairRequestDtoMapper.toCloseResponse(
                        repairRequestService.close(id, request.getRepairResult())
                )
        );
    }
}

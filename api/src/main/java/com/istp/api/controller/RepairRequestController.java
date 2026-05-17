package com.istp.api.controller;

import com.istp.api.dto.request.CreateRepairRequest;
import com.istp.api.dto.request.UserCommentUpdateRequest;
import com.istp.api.dto.response.CancelRepairRequestResponse;
import com.istp.api.dto.response.RepairRequestDetailsResponse;
import com.istp.api.dto.response.UserCommentResponse;
import com.istp.api.dto.response.UserRepairRequestSummaryResponse;
import com.istp.api.service.api.RepairRequestService;
import com.istp.api.service.mapper.RepairRequestDtoMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RepairRequestController {
    private final RepairRequestService repairRequestService;
    private final RepairRequestDtoMapper repairRequestDtoMapper;

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UserRepairRequestSummaryResponse>> getMyRequests() {
        return ResponseEntity.ok(repairRequestDtoMapper.toUserSummaryResponses(repairRequestService.getMyRequests()));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RepairRequestDetailsResponse> create(@Valid @RequestBody CreateRepairRequest request) {
        RepairRequestDetailsResponse response = repairRequestDtoMapper.toDetailsResponse(
                repairRequestService.create(repairRequestDtoMapper.toModel(request))
        );
        return ResponseEntity.created(URI.create("/api/requests/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'TECHNICIAN')")
    public ResponseEntity<RepairRequestDetailsResponse> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(repairRequestDtoMapper.toDetailsResponse(repairRequestService.getDetails(id)));
    }

    @PatchMapping("/{id}/user-comment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserCommentResponse> updateUserComment(
            @PathVariable Long id,
            @RequestBody UserCommentUpdateRequest request
    ) {
        return ResponseEntity.ok(
                repairRequestDtoMapper.toUserCommentResponse(
                        repairRequestService.updateUserComment(id, request.getUserComment())
                )
        );
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CancelRepairRequestResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(repairRequestDtoMapper.toCancelResponse(repairRequestService.cancel(id)));
    }
}

package com.istp.api.service.impl;

import com.istp.api.common.EquipmentType;
import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import com.istp.api.common.UserRole;
import com.istp.api.dao.persistence.entity.RepairRequestEntity;
import com.istp.api.dao.persistence.mapper.RepairRequestPersistenceMapper;
import com.istp.api.dao.persistence.repository.RepairRequestRepository;
import com.istp.api.exception.BusinessRuleException;
import com.istp.api.exception.ForbiddenException;
import com.istp.api.exception.NotFoundException;
import com.istp.api.security.AuthenticatedUser;
import com.istp.api.security.CurrentUserProvider;
import com.istp.api.service.api.RepairRequestService;
import com.istp.api.service.model.RepairRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepairRequestServiceImpl implements RepairRequestService {
    private final RepairRequestRepository repairRequestRepository;
    private final RepairRequestPersistenceMapper repairRequestMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public List<RepairRequest> getMyRequests() {
        Long userId = currentUserProvider.getCurrentUserId();
        return repairRequestMapper.toModels(repairRequestRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @Override
    @Transactional
    public RepairRequest create(RepairRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();
        validateEquipmentName(request);

        request.setId(null);
        request.setUserId(userId);
        request.setTechnicianId(null);
        request.setStatus(RequestStatus.CREATED);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(null);
        request.setCancelledAt(null);
        request.setClosedAt(null);
        if (request.getPriority() == null) {
            request.setPriority(RequestPriority.MEDIUM);
        }

        RepairRequestEntity saved = repairRequestRepository.save(repairRequestMapper.toEntity(request));
        return repairRequestMapper.toModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RepairRequest getDetails(Long id) {
        RepairRequest request = findById(id);
        AuthenticatedUser user = currentUserProvider.getCurrentUser();
        if (user.role() == UserRole.USER && !request.getUserId().equals(user.id())) {
            throw new ForbiddenException("User has no access to this repair request");
        }
        return request;
    }

    @Override
    @Transactional
    public RepairRequest updateUserComment(Long id, String userComment) {
        RepairRequest request = findById(id);
        requireCurrentUserOwns(request);
        requireMutable(request);

        request.setUserComment(userComment);
        request.setUpdatedAt(LocalDateTime.now());
        return save(request);
    }

    @Override
    @Transactional
    public RepairRequest cancel(Long id) {
        RepairRequest request = findById(id);
        requireCurrentUserOwns(request);
        requireMutable(request);

        LocalDateTime now = LocalDateTime.now();
        request.setStatus(RequestStatus.CANCELLED);
        request.setCancelledAt(now);
        request.setUpdatedAt(now);
        return save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairRequest> getTechnicianRequests(RequestStatus status, RequestPriority priority) {
        if (status != null && priority != null) {
            return repairRequestMapper.toModels(
                    repairRequestRepository.findByStatusAndPriorityOrderByCreatedAtDesc(status, priority)
            );
        }
        if (status != null) {
            return repairRequestMapper.toModels(repairRequestRepository.findByStatusOrderByCreatedAtDesc(status));
        }
        if (priority != null) {
            return repairRequestMapper.toModels(repairRequestRepository.findByPriorityOrderByCreatedAtDesc(priority));
        }
        return repairRequestMapper.toModels(repairRequestRepository.findAllByOrderByCreatedAtDesc());
    }

    @Override
    @Transactional
    public RepairRequest take(Long id) {
        RepairRequest request = findById(id);
        if (request.getStatus() != RequestStatus.CREATED || request.getTechnicianId() != null) {
            throw new BusinessRuleException("Only created repair requests can be taken");
        }

        Long technicianId = currentUserProvider.getCurrentUserId();
        request.setTechnicianId(technicianId);
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setUpdatedAt(LocalDateTime.now());
        return save(request);
    }

    @Override
    @Transactional
    public RepairRequest updateTechnicianStatus(Long id, RequestStatus status) {
        if (status != RequestStatus.IN_PROGRESS && status != RequestStatus.WAITING_FOR_USER) {
            throw new BusinessRuleException("Technician can set only IN_PROGRESS or WAITING_FOR_USER status");
        }

        RepairRequest request = findById(id);
        requireAssignedToCurrentTechnician(request);
        requireMutable(request);

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());
        return save(request);
    }

    @Override
    @Transactional
    public RepairRequest updateTechnicianComment(Long id, String technicianComment) {
        RepairRequest request = findById(id);
        requireAssignedToCurrentTechnician(request);
        requireMutable(request);

        request.setTechnicianComment(technicianComment);
        request.setUpdatedAt(LocalDateTime.now());
        return save(request);
    }

    @Override
    @Transactional
    public RepairRequest updateRepairResult(Long id, String repairResult) {
        if (isBlank(repairResult)) {
            throw new BusinessRuleException("Repair result must not be blank");
        }

        RepairRequest request = findById(id);
        requireAssignedToCurrentTechnician(request);
        requireMutable(request);

        request.setRepairResult(repairResult);
        request.setUpdatedAt(LocalDateTime.now());
        return save(request);
    }

    @Override
    @Transactional
    public RepairRequest close(Long id, String repairResult) {
        if (isBlank(repairResult)) {
            throw new BusinessRuleException("Repair result must not be blank");
        }

        RepairRequest request = findById(id);
        requireAssignedToCurrentTechnician(request);
        requireMutable(request);

        LocalDateTime now = LocalDateTime.now();
        request.setRepairResult(repairResult);
        request.setStatus(RequestStatus.COMPLETED);
        request.setClosedAt(now);
        request.setUpdatedAt(now);
        return save(request);
    }

    private RepairRequest findById(Long id) {
        return repairRequestRepository.findById(id)
                .map(repairRequestMapper::toModel)
                .orElseThrow(() -> new NotFoundException("Repair request not found"));
    }

    private RepairRequest save(RepairRequest request) {
        return repairRequestMapper.toModel(repairRequestRepository.save(repairRequestMapper.toEntity(request)));
    }

    private void requireCurrentUserOwns(RepairRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();
        if (!request.getUserId().equals(userId)) {
            throw new ForbiddenException("This repair request belongs to another user");
        }
    }

    private void requireAssignedToCurrentTechnician(RepairRequest request) {
        Long technicianId = currentUserProvider.getCurrentUserId();
        if (!technicianId.equals(request.getTechnicianId())) {
            throw new ForbiddenException("This repair request is not assigned to the current technician");
        }
    }

    private void requireMutable(RepairRequest request) {
        if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new BusinessRuleException("Completed or cancelled repair requests cannot be changed");
        }
    }

    private void validateEquipmentName(RepairRequest request) {
        if (request.getEquipmentType() == EquipmentType.OTHER && isBlank(request.getEquipmentName())) {
            throw new BusinessRuleException("Equipment name is required for OTHER equipment type");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

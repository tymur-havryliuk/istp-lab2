package com.istp.api.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.istp.api.common.EquipmentType;
import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import com.istp.api.common.UserRole;
import com.istp.api.dao.persistence.entity.RepairRequestEntity;
import com.istp.api.dao.persistence.mapper.RepairRequestPersistenceMapper;
import com.istp.api.dao.persistence.repository.RepairRequestRepository;
import com.istp.api.exception.BusinessRuleException;
import com.istp.api.exception.ForbiddenException;
import com.istp.api.security.AuthenticatedUser;
import com.istp.api.security.CurrentUserProvider;
import com.istp.api.service.model.RepairRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepairRequestServiceImplTest {
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long TECHNICIAN_ID = 3L;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private RepairRequestServiceImpl repairRequestService;

    @BeforeEach
    void setUp() {
        RepairRequestPersistenceMapper mapper = Mappers.getMapper(RepairRequestPersistenceMapper.class);
        repairRequestService = new RepairRequestServiceImpl(repairRequestRepository, mapper, currentUserProvider);
    }

    @Test
    void userCreatesRepairRequest() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(repairRequestRepository.save(any())).thenAnswer(invocation -> {
            RepairRequestEntity entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        RepairRequest created = repairRequestService.create(RepairRequest.builder()
                .equipmentType(EquipmentType.PRINTER)
                .equipmentName("HP LaserJet")
                .title("Printer does not print")
                .problemDescription("Printer does not react to print jobs")
                .priority(RequestPriority.MEDIUM)
                .build());

        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getUserId()).isEqualTo(USER_ID);
        assertThat(created.getTechnicianId()).isNull();
        assertThat(created.getStatus()).isEqualTo(RequestStatus.CREATED);
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void userViewsOnlyOwnRepairRequests() {
        RepairRequestEntity ownRequest = requestEntity(10L, USER_ID, null, RequestStatus.CREATED);
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(repairRequestRepository.findByUserIdOrderByCreatedAtDesc(USER_ID)).thenReturn(List.of(ownRequest));

        List<RepairRequest> requests = repairRequestService.getMyRequests();

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getUserId()).isEqualTo(USER_ID);
        verify(repairRequestRepository).findByUserIdOrderByCreatedAtDesc(USER_ID);
    }

    @Test
    void userCannotAccessAnotherUsersRepairRequest() {
        when(currentUserProvider.getCurrentUser()).thenReturn(authenticatedUser(USER_ID, UserRole.USER));
        when(repairRequestRepository.findById(10L))
                .thenReturn(Optional.of(requestEntity(10L, OTHER_USER_ID, null, RequestStatus.CREATED)));

        assertThatThrownBy(() -> repairRequestService.getDetails(10L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void userCancelsOwnRepairRequest() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(repairRequestRepository.findById(10L))
                .thenReturn(Optional.of(requestEntity(10L, USER_ID, null, RequestStatus.CREATED)));
        when(repairRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RepairRequest cancelled = repairRequestService.cancel(10L);

        assertThat(cancelled.getStatus()).isEqualTo(RequestStatus.CANCELLED);
        assertThat(cancelled.getCancelledAt()).isNotNull();
        assertThat(cancelled.getUpdatedAt()).isNotNull();
    }

    @Test
    void technicianTakesCreatedRepairRequest() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(TECHNICIAN_ID);
        when(repairRequestRepository.findById(10L))
                .thenReturn(Optional.of(requestEntity(10L, USER_ID, null, RequestStatus.CREATED)));
        when(repairRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RepairRequest taken = repairRequestService.take(10L);

        assertThat(taken.getTechnicianId()).isEqualTo(TECHNICIAN_ID);
        assertThat(taken.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(taken.getUpdatedAt()).isNotNull();
    }

    @Test
    void technicianCannotTakeAlreadyTakenRepairRequest() {
        when(repairRequestRepository.findById(10L))
                .thenReturn(Optional.of(requestEntity(10L, USER_ID, TECHNICIAN_ID, RequestStatus.IN_PROGRESS)));

        assertThatThrownBy(() -> repairRequestService.take(10L))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void technicianChangesStatusOnlyForAssignedRepairRequest() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(TECHNICIAN_ID);
        when(repairRequestRepository.findById(10L))
                .thenReturn(Optional.of(requestEntity(10L, USER_ID, TECHNICIAN_ID, RequestStatus.IN_PROGRESS)));
        when(repairRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RepairRequest updated = repairRequestService.updateTechnicianStatus(10L, RequestStatus.WAITING_FOR_USER);

        assertThat(updated.getStatus()).isEqualTo(RequestStatus.WAITING_FOR_USER);
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void technicianCannotChangeStatusForAnotherTechniciansRepairRequest() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(99L);
        when(repairRequestRepository.findById(10L))
                .thenReturn(Optional.of(requestEntity(10L, USER_ID, TECHNICIAN_ID, RequestStatus.IN_PROGRESS)));

        assertThatThrownBy(() -> repairRequestService.updateTechnicianStatus(10L, RequestStatus.WAITING_FOR_USER))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void technicianCannotCloseRepairRequestWithoutRepairResult() {
        assertThatThrownBy(() -> repairRequestService.close(10L, " "))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void technicianClosesRepairRequestWithRepairResult() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(TECHNICIAN_ID);
        when(repairRequestRepository.findById(10L))
                .thenReturn(Optional.of(requestEntity(10L, USER_ID, TECHNICIAN_ID, RequestStatus.IN_PROGRESS)));
        when(repairRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RepairRequest closed = repairRequestService.close(10L, "Power cable replaced");

        assertThat(closed.getStatus()).isEqualTo(RequestStatus.COMPLETED);
        assertThat(closed.getRepairResult()).isEqualTo("Power cable replaced");
        assertThat(closed.getClosedAt()).isNotNull();
        assertThat(closed.getUpdatedAt()).isNotNull();
    }

    private RepairRequestEntity requestEntity(
            Long id,
            Long userId,
            Long technicianId,
            RequestStatus status
    ) {
        RepairRequestEntity entity = new RepairRequestEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setTechnicianId(technicianId);
        entity.setEquipmentType(EquipmentType.PRINTER);
        entity.setEquipmentName("HP LaserJet");
        entity.setTitle("Printer issue");
        entity.setProblemDescription("Printer does not print");
        entity.setStatus(status);
        entity.setPriority(RequestPriority.MEDIUM);
        entity.setCreatedAt(LocalDateTime.now().minusDays(1));
        return entity;
    }

    private AuthenticatedUser authenticatedUser(Long id, UserRole role) {
        return new AuthenticatedUser() {
            @Override
            public Long id() {
                return id;
            }

            @Override
            public String email() {
                return "user@example.com";
            }

            @Override
            public UserRole role() {
                return role;
            }
        };
    }
}

package com.istp.api.dao.persistence.repository;

import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import com.istp.api.dao.persistence.entity.RepairRequestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairRequestRepository extends JpaRepository<RepairRequestEntity, Long> {
    List<RepairRequestEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<RepairRequestEntity> findAllByOrderByCreatedAtDesc();

    List<RepairRequestEntity> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    List<RepairRequestEntity> findByPriorityOrderByCreatedAtDesc(RequestPriority priority);

    List<RepairRequestEntity> findByStatusAndPriorityOrderByCreatedAtDesc(
            RequestStatus status,
            RequestPriority priority
    );
}

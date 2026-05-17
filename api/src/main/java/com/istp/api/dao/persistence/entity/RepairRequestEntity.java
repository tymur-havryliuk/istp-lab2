package com.istp.api.dao.persistence.entity;

import com.istp.api.common.EquipmentType;
import com.istp.api.common.RequestPriority;
import com.istp.api.common.RequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "repair_requests")
public class RepairRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "technician_id")
    private Long technicianId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "equipment_type", nullable = false, columnDefinition = "equipment_type")
    private EquipmentType equipmentType;

    @Column(name = "equipment_name", length = 150)
    private String equipmentName;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "problem_description", nullable = false)
    private String problemDescription;

    @Column(name = "user_comment")
    private String userComment;

    @Column(name = "technician_comment")
    private String technicianComment;

    @Column(name = "repair_result")
    private String repairResult;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "request_status")
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "request_priority")
    private RequestPriority priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}

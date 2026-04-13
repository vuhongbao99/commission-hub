package com.commissionhub.commission_hub.entity;

import com.commissionhub.commission_hub.enums.AdjustmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "commission_adjustments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommissionAdjustment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private CommissionTask task;

    @Column(name = "old_amount", nullable = false)
    private Long oldAmount;

    @Column(name = "new_amount", nullable = false)
    private Long newAmount;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AdjustmentStatus status = AdjustmentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
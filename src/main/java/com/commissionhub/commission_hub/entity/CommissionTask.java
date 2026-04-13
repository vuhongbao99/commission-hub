package com.commissionhub.commission_hub.entity;

import com.commissionhub.commission_hub.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "commission_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommissionTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_id", nullable = false)
    private Commission commission;

    @Column(name = "task_name", nullable = false, length = 255)
    private String taskName;

    @Column(name = "task_link", columnDefinition = "TEXT")
    private String taskLink;

    @Column(name = "commission_amount", nullable = false)
    private Long commissionAmount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "task_created_date", nullable = false)
    private LocalDate taskCreatedDate;

    @Column(name = "task_completed_date")
    private LocalDate taskCompletedDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.IN_PROGRESS;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private PaymentVoucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferred_to")
    private User transferredTo;
}
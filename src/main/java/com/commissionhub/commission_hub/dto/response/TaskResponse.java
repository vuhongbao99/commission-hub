package com.commissionhub.commission_hub.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class TaskResponse {
    private String id;
    private String taskName;
    private String taskLink;
    private Long commissionAmount;
    private String assignedToName;
    private LocalDate taskCreatedDate;
    private LocalDate taskCompletedDate;
    private LocalDateTime completedAt;
    private String status;
    private String rejectionReason;
    private LocalDate paymentDate;
    private Long overdueDays;
}
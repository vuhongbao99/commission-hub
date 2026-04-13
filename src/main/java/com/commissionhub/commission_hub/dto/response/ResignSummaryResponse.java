package com.commissionhub.commission_hub.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder
public class ResignSummaryResponse {
    private String userId;
    private String fullName;
    private String resignedAt;
    private List<TaskResponse> pendingTasks;    // Chờ duyệt
    private List<TaskResponse> approvedTasks;   // Đã duyệt chưa TT
    private List<TaskResponse> inProgressTasks; // Đang làm dở
    private Long totalPendingAmount;
}
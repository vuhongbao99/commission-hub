package com.commissionhub.commission_hub.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @Builder
public class CommissionResponse {
    private String id;
    private String userId;
    private String fullName;
    private Integer periodMonth;
    private Integer periodYear;
    private Long revenue;
    private BigDecimal rate;
    private Long amount;
    private String status;
    private List<TaskResponse> tasks;
}
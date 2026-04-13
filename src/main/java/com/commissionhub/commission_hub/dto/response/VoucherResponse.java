package com.commissionhub.commission_hub.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @Builder
public class VoucherResponse {
    private String id;
    private String voucherCode;
    private String userFullName;
    private String bankName;
    private String bankAccount;
    private Long totalAmount;
    private Boolean isMatched;
    private LocalDateTime paidAt;
    private List<TaskResponse> tasks;
}
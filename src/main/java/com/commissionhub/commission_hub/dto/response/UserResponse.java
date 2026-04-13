package com.commissionhub.commission_hub.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private String role;
    private String teamId;
    private String teamName;
    private BigDecimal commissionRate;
    private String bankName;
    private String bankAccount;
    private String status;
}
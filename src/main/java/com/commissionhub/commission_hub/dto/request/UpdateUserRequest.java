package com.commissionhub.commission_hub.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
public class UpdateUserRequest {
    private String fullName;
    private String teamId;
    private BigDecimal commissionRate;
    private String bankName;
    private String bankAccount;
    private String bankBranch;
}
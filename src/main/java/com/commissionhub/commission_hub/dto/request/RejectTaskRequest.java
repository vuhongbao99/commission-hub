package com.commissionhub.commission_hub.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
public class RejectTaskRequest {

    @NotBlank(message = "Lý do từ chối không được để trống")
    private String rejectionReason;
}
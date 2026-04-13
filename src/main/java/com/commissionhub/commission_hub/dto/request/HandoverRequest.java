package com.commissionhub.commission_hub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandoverRequest {

    @NotBlank
    private String taskId;

    @NotNull
    private HandoverAction action;

    private String transferToUserId; // Nếu action = TRANSFER

    public enum HandoverAction {
        PAY_NOW,    // Thanh toán ngay
        TRANSFER,   // Chuyển giao
        CANCEL      // Huỷ
    }
}
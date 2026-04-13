package com.commissionhub.commission_hub.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter @Setter
public class CreateVoucherRequest {

    @NotEmpty(message = "Danh sách task không được để trống")
    private List<String> taskIds;
}
package com.commissionhub.commission_hub.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class CreateCommissionRequest {

    @NotNull(message = "Tháng không được để trống")
    @Min(value = 1) @Max(value = 12)
    private Integer periodMonth;

    @NotNull(message = "Năm không được để trống")
    private Integer periodYear;

    @NotNull(message = "Doanh thu không được để trống")
    @Min(value = 0)
    private Long revenue;

    private String note;
}

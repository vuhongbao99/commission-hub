package com.commissionhub.commission_hub.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
public class CreateTaskRequest {

    @NotBlank(message = "Tên task không được để trống")
    private String taskName;

    private String taskLink;

    @NotNull(message = "Số tiền hoa hồng không được để trống")
    @Min(value = 0)
    private Long commissionAmount;

    @NotNull(message = "Ngày tạo task không được để trống")
    private LocalDate taskCreatedDate;
}
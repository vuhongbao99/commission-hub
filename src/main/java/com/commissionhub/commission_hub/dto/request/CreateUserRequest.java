package com.commissionhub.commission_hub.dto.request;

import com.commissionhub.commission_hub.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
public class CreateUserRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank @Email
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @NotNull
    private Role role;

    private String teamId;

    private BigDecimal commissionRate = BigDecimal.valueOf(10.00);

    private String bankName;
    private String bankAccount;
    private String bankBranch;
}
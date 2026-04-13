package com.commissionhub.commission_hub.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class LoginRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;
}
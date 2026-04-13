package com.commissionhub.commission_hub.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
public class CreateTeamRequest {

    @NotBlank(message = "Tên team không được để trống")
    private String teamName;
}
package com.commissionhub.commission_hub.dto.response;

import lombok.*;

@Getter @Setter @Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String fullName;
    private String role;
}
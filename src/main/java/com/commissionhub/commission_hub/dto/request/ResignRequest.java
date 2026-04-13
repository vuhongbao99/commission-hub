package com.commissionhub.commission_hub.dto.request;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
public class ResignRequest {
    private LocalDate resignedAt;
}
package com.commissionhub.commission_hub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "bank_transfer_results")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BankTransferResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private BulkPaymentBatch batch;

    @Column(name = "voucher_code", nullable = false, length = 50)
    private String voucherCode;

    @Column(name = "bank_ref_no", length = 100)
    private String bankRefNo;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "error_reason", length = 500)
    private String errorReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
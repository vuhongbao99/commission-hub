package com.commissionhub.commission_hub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "payment_vouchers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentVoucher extends BaseEntity {

    @Column(name = "voucher_code", nullable = false, unique = true, length = 50)
    private String voucherCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private BulkPaymentBatch batch;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount = 0L;

    @Column(name = "bill_url", columnDefinition = "TEXT")
    private String billUrl;

    @Column(name = "bill_amount")
    private Long billAmount;

    @Column(name = "is_matched")
    private Boolean isMatched;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
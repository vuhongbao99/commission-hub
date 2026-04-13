package com.commissionhub.commission_hub.entity;

import com.commissionhub.commission_hub.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "bulk_payment_batches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BulkPaymentBatch extends BaseEntity {

    @Column(name = "quarter", nullable = false, columnDefinition = "TINYINT")
    private Integer quarter;

    @Column(name = "year", nullable = false, columnDefinition = "SMALLINT")
    private Integer year;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount = 0L;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private VoucherStatus status = VoucherStatus.DRAFT;

    @Column(name = "exported_at")
    private LocalDateTime exportedAt;

    @Column(name = "result_imported_at")
    private LocalDateTime resultImportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
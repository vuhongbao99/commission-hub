package com.commissionhub.commission_hub.entity;


import com.commissionhub.commission_hub.enums.CommissionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "commissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","period_month","period_year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "revenue", nullable = false)
    private Long revenue = 0L;

    @Column(name = "rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal rate;

    @Column(name = "amount", nullable = false)
    private Long amount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CommissionStatus status = CommissionStatus.DRAFT;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "commission", fetch = FetchType.LAZY)
    private List<CommissionTask> tasks;
}
package com.commissionhub.commission_hub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "okrs",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"year", "target_type", "target_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Okr extends BaseEntity {

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "target_type", nullable = false, length = 10)
    private String targetType;

    @Column(name = "target_id", nullable = false, length = 36)
    private String targetId;

    @Column(name = "revenue_target", nullable = false)
    private Long revenueTarget = 0L;

    @Column(name = "task_target", nullable = false)
    private Integer taskTarget = 0;

    @Column(name = "commission_target", nullable = false)
    private Long commissionTarget = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
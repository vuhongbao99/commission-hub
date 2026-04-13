package com.commissionhub.commission_hub.repository;

import com.commissionhub.commission_hub.entity.BulkPaymentBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkPaymentBatchRepository extends JpaRepository<BulkPaymentBatch, String> {
}
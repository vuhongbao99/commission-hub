package com.commissionhub.commission_hub.repository;

import com.commissionhub.commission_hub.entity.PaymentVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, String> {
    Optional<PaymentVoucher> findByVoucherCode(String voucherCode);
    List<PaymentVoucher> findByUserId(String userId);
    List<PaymentVoucher> findByBatchId(String batchId);
}
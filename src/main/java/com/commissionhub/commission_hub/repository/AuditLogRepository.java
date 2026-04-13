package com.commissionhub.commission_hub.repository;

import com.commissionhub.commission_hub.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    List<AuditLog> findByTableNameAndRecordId(String tableName, String recordId);
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(String userId);
}
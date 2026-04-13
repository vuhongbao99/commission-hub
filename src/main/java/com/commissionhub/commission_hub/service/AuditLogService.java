package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.entity.AuditLog;
import com.commissionhub.commission_hub.entity.User;
import com.commissionhub.commission_hub.repository.AuditLogRepository;
import com.commissionhub.commission_hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public void log(String action, String tableName, String recordId,
                    String oldValue, String newValue) {
        try {
            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElse(null);

            AuditLog log = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .tableName(tableName)
                    .recordId(recordId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build();
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Không để lỗi audit làm crash request chính
        }
    }
}
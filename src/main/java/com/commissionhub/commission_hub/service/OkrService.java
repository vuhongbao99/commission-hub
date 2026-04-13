package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.entity.Okr;
import com.commissionhub.commission_hub.entity.User;
import com.commissionhub.commission_hub.repository.CommissionRepository;
import com.commissionhub.commission_hub.repository.OkrRepository;
import com.commissionhub.commission_hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OkrService {

    private final OkrRepository okrRepository;
    private final UserRepository userRepository;
    private final CommissionRepository commissionRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    @Transactional
    public Okr createOrUpdate(Integer year, String targetType,
                              String targetId, Long revenueTarget,
                              Integer taskTarget, Long commissionTarget) {
        User director = getCurrentUser();

        Okr okr = okrRepository
                .findByYearAndTargetTypeAndTargetId(year, targetType, targetId)
                .orElse(Okr.builder()
                        .year(year)
                        .targetType(targetType)
                        .targetId(targetId)
                        .createdBy(director)
                        .build());

        okr.setRevenueTarget(revenueTarget);
        okr.setTaskTarget(taskTarget);
        okr.setCommissionTarget(commissionTarget);
        return okrRepository.save(okr);
    }

    public Map<String, Object> getProgress(Integer year, String userId) {
        Okr okr = okrRepository
                .findByYearAndTargetTypeAndTargetId(year, "USER", userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OKR"));

        Long actualRevenue = commissionRepository
                .sumAmountByUserAndYear(userId, year);

        Map<String, Object> result = new HashMap<>();
        result.put("revenueTarget", okr.getRevenueTarget());
        result.put("revenueActual", actualRevenue != null ? actualRevenue : 0);
        result.put("revenuePercent", actualRevenue != null
                ? (actualRevenue * 100.0 / okr.getRevenueTarget()) : 0);
        result.put("commissionTarget", okr.getCommissionTarget());
        result.put("taskTarget", okr.getTaskTarget());
        return result;
    }
}
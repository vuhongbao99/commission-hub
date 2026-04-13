package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.enums.TaskStatus;
import com.commissionhub.commission_hub.repository.CommissionRepository;
import com.commissionhub.commission_hub.repository.CommissionTaskRepository;
import com.commissionhub.commission_hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final CommissionRepository commissionRepository;
    private final CommissionTaskRepository commissionTaskRepository;
    private final UserRepository userRepository;

    // Doanh thu theo tháng trong năm
    public Map<String, Object> revenueByMonth(Integer year) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            final int m = month;
            long total = commissionRepository.findAll().stream()
                    .filter(c -> c.getPeriodYear().equals(year) && c.getPeriodMonth() == m)
                    .mapToLong(c -> c.getRevenue())
                    .sum();
            Map<String, Object> item = new HashMap<>();
            item.put("month", "T" + month);
            item.put("revenue", total);
            data.add(item);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("data", data);
        return result;
    }

    // Top nhân viên theo hoa hồng
    public List<Map<String, Object>> topEmployees(Integer year) {
        return userRepository.findAll().stream()
                .map(user -> {
                    Long total = commissionRepository
                            .sumAmountByUserAndYear(user.getId(), year);
                    Map<String, Object> item = new HashMap<>();
                    item.put("userId", user.getId());
                    item.put("fullName", user.getFullName());
                    item.put("totalCommission", total != null ? total : 0);
                    return item;
                })
                .sorted((a, b) -> Long.compare(
                        (Long) b.get("totalCommission"),
                        (Long) a.get("totalCommission")))
                .limit(10)
                .toList();
    }

    // Tổng quan dashboard
    public Map<String, Object> dashboard() {
        long totalTasks = commissionTaskRepository.count();
        long pendingTasks = commissionTaskRepository.findByStatus(TaskStatus.PENDING).size();
        long paidTasks = commissionTaskRepository.findByStatus(TaskStatus.PAID).size();
        long overdueTasks = commissionTaskRepository.findOverdueTasks(30).size();

        Map<String, Object> result = new HashMap<>();
        result.put("totalTasks", totalTasks);
        result.put("pendingTasks", pendingTasks);
        result.put("paidTasks", paidTasks);
        result.put("overdueTasks", overdueTasks);
        return result;
    }
}
package com.commissionhub.commission_hub.controller;

import com.commissionhub.commission_hub.dto.request.CreateCommissionRequest;
import com.commissionhub.commission_hub.dto.request.CreateTaskRequest;
import com.commissionhub.commission_hub.dto.response.CommissionResponse;
import com.commissionhub.commission_hub.dto.response.TaskResponse;
import com.commissionhub.commission_hub.service.CommissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;

    // Accountant tạo hoa hồng tháng
    @PostMapping
    @PreAuthorize("hasAnyRole('ACCOUNTANT','DIRECTOR','ADMIN')")
    public ResponseEntity<CommissionResponse> create(
            @Valid
            @RequestBody CreateCommissionRequest request) {
        return ResponseEntity.ok(commissionService.createCommission(request));
    }

    // Thêm task vào hoa hồng
    @PostMapping("/{commissionId}/tasks")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','DIRECTOR','ADMIN')")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable String commissionId,
            @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(commissionService.createTask(commissionId, request));
    }

    // Xem lịch sử hoa hồng cá nhân
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','DIRECTOR','ADMIN')")
    public ResponseEntity<List<CommissionResponse>> getMyCommissions() {
        return ResponseEntity.ok(commissionService.getMyCommissions());
    }
}
package com.commissionhub.commission_hub.controller;

import com.commissionhub.commission_hub.dto.request.RejectTaskRequest;
import com.commissionhub.commission_hub.dto.response.TaskResponse;
import com.commissionhub.commission_hub.service.CommissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CommissionService commissionService;

    // Nhân viên nhấn hoàn thành
    @PutMapping("/{taskId}/complete")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','DIRECTOR','ADMIN')")
    public ResponseEntity<TaskResponse> complete(
            @PathVariable String taskId,
            @RequestParam(required = false) String completedDate) {
        LocalDate date = completedDate != null
                ? LocalDate.parse(completedDate)
                : LocalDate.now();
        return ResponseEntity.ok(commissionService.completeTask(taskId, date));
    }

    // Director duyệt task
    @PutMapping("/{taskId}/approve")
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<TaskResponse> approve(@PathVariable String taskId) {
        return ResponseEntity.ok(commissionService.approveTask(taskId));
    }

    // Director từ chối task
    @PutMapping("/{taskId}/reject")
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<TaskResponse> reject(
            @PathVariable String taskId,
            @Valid @RequestBody RejectTaskRequest request) {
        return ResponseEntity.ok(commissionService.rejectTask(taskId, request));
    }

    // Director xem danh sách task chờ duyệt
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<List<TaskResponse>> getPending() {
        return ResponseEntity.ok(commissionService.getPendingTasks());
    }

    // Xem chi tiết task
    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','DIRECTOR','ADMIN')")
    public ResponseEntity<TaskResponse> getById(@PathVariable String taskId) {
        return ResponseEntity.ok(commissionService.getTaskById(taskId));
    }

    // Danh sách task chậm tiến độ
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<List<TaskResponse>> getOverdue() {
        return ResponseEntity.ok(commissionService.getOverdueTasks());
    }
}
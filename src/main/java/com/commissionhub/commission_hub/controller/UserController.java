package com.commissionhub.commission_hub.controller;

import com.commissionhub.commission_hub.dto.request.*;
import com.commissionhub.commission_hub.dto.response.ResignSummaryResponse;
import com.commissionhub.commission_hub.dto.response.UserResponse;
import com.commissionhub.commission_hub.entity.Team;
import com.commissionhub.commission_hub.service.AdminService;
import com.commissionhub.commission_hub.service.ResignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ResignService resignService;
    private final AdminService adminService;

    // Admin đánh dấu nhân viên nghỉ việc
    @PutMapping("/{userId}/resign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResignSummaryResponse> resign(
            @PathVariable String userId,
            @RequestBody ResignRequest request) {
        return ResponseEntity.ok(resignService.resignUser(userId, request));
    }

    // Director xử lý từng khoản tồn đọng
    @PostMapping("/handover")
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<String> handover(
            @Valid @RequestBody HandoverRequest request) {
        return ResponseEntity.ok(resignService.handleHandover(request));
    }

    // ============================================================
    // USER
    // ============================================================
    @GetMapping("/admin/users")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PutMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminService.updateUser(userId, request));
    }

    @PutMapping("/admin/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable String userId,
            @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateStatus(userId, status));
    }



    // ============================================================
    // TEAM
    // ============================================================
    @GetMapping("/admin/teams")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(adminService.getAllTeams());
    }

    @PostMapping("/admin/teams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Team> createTeam(
            @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.ok(adminService.createTeam(request));
    }
}
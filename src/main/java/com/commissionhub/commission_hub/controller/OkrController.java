package com.commissionhub.commission_hub.controller;

import com.commissionhub.commission_hub.entity.Okr;
import com.commissionhub.commission_hub.service.OkrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/okrs")
@RequiredArgsConstructor
public class OkrController {

    private final OkrService okrService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<Okr> createOrUpdate(
            @RequestParam Integer year,
            @RequestParam String targetType,
            @RequestParam String targetId,
            @RequestParam Long revenueTarget,
            @RequestParam Integer taskTarget,
            @RequestParam Long commissionTarget) {
        return ResponseEntity.ok(okrService.createOrUpdate(
                year, targetType, targetId,
                revenueTarget, taskTarget, commissionTarget));
    }

    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getProgress(
            @RequestParam Integer year,
            @RequestParam String userId) {
        return ResponseEntity.ok(okrService.getProgress(year, userId));
    }
}
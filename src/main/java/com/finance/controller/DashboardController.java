package com.finance.controller;
import com.finance.service.DashboardService; 
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.finance.dto.DashboardSummaryResponse;

@RestController
@RequestMapping("/api/dashboard")
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary(@RequestAttribute String username) {
        try {
            DashboardSummaryResponse summary = dashboardService.getDashboardSummary(username);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error fetching dashboard summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
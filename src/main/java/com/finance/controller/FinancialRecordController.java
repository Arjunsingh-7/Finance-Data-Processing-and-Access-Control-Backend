package com.finance.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.finance.dto.FinancialRecordRequest;
import com.finance.dto.FinancialRecordResponse;
import com.finance.service.FinancialRecordService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class FinancialRecordController {
    @Autowired
    private FinancialRecordService recordService;

    @PostMapping
    public ResponseEntity<?> createRecord(@Valid @RequestBody FinancialRecordRequest request,
                                          @RequestAttribute String username) {
        try {
            FinancialRecordResponse record = recordService.createRecord(username, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        } catch (Exception e) {
            log.error("Error creating record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecordById(@PathVariable Long id,
                                          @RequestAttribute String username) {
        try {
            FinancialRecordResponse record = recordService.getRecordById(id, username);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserRecords(@RequestAttribute String username) {
        try {
            List<FinancialRecordResponse> records = recordService.getUserRecords(username);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("Error fetching records: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/by-type")
    public ResponseEntity<?> getRecordsByType(@RequestParam String type,
                                             @RequestAttribute String username) {
        try {
            List<FinancialRecordResponse> records = recordService.getRecordsByType(username, type);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/by-category")
    public ResponseEntity<?> getRecordsByCategory(@RequestParam String category,
                                                 @RequestAttribute String username) {
        try {
            List<FinancialRecordResponse> records = recordService.getRecordsByCategory(username, category);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("Error fetching records by category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestAttribute String username) {
        try {
            List<FinancialRecordResponse> records = recordService.getRecordsByDateRange(username, startDate, endDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("Error fetching records by date range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable Long id,
                                         @Valid @RequestBody FinancialRecordRequest request,
                                         @RequestAttribute String username) {
        try {
            FinancialRecordResponse record = recordService.updateRecord(id, username, request);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id,
                                         @RequestAttribute String username) {
        try {
            recordService.deleteRecord(id, username);
            return ResponseEntity.ok("Record deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
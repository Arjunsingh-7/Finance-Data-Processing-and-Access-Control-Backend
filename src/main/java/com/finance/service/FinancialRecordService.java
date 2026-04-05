package com.finance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 import com.finance.dto.FinancialRecordRequest;
import com.finance.dto.FinancialRecordRequest;
import com.finance.dto.FinancialRecordResponse;
import com.finance.entity.FinancialRecord;
import com.finance.entity.User;
import com.finance.repository.FinancialRecordRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
 import com.finance.dto.FinancialRecordResponse;

@Service
@Transactional
@Slf4j
public class FinancialRecordService {
    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private UserService userService;

    public FinancialRecordResponse createRecord(String username, FinancialRecordRequest request) {
        validateRecordType(request.getType());

        User user = userService.getUserEntityByUsername(username);

        FinancialRecord record = FinancialRecord.builder()
                .user(user)
                .amount(request.getAmount())
                .type(request.getType().toUpperCase())
                .category(request.getCategory())
                .recordDate(request.getRecordDate())
                .notes(request.getNotes())
                .isDeleted(false)
                .build();

        record = recordRepository.save(record);
        log.info("Financial record created: id={}, user={}, type={}", record.getId(), username, request.getType());
        return mapToResponse(record);
    }

    public FinancialRecordResponse getRecordById(Long id, String username) {
        User user = userService.getUserEntityByUsername(username);
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Record does not belong to user");
        }

        return mapToResponse(record);
    }

    public List<FinancialRecordResponse> getUserRecords(String username) {
        User user = userService.getUserEntityByUsername(username);
        return recordRepository.findByUserAndIsDeletedFalseOrderByRecordDateDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FinancialRecordResponse> getRecordsByType(String username, String type) {
        validateRecordType(type);
        User user = userService.getUserEntityByUsername(username);
        return recordRepository.findByUserAndTypeAndIsDeletedFalse(user, type.toUpperCase()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FinancialRecordResponse> getRecordsByCategory(String username, String category) {
        User user = userService.getUserEntityByUsername(username);
        return recordRepository.findByUserAndCategoryAndIsDeletedFalse(user, category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FinancialRecordResponse> getRecordsByDateRange(String username, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserEntityByUsername(username);
        return recordRepository.findByUserAndRecordDateBetweenAndIsDeletedFalse(user, startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FinancialRecordResponse updateRecord(Long id, String username, FinancialRecordRequest request) {
        validateRecordType(request.getType());
        User user = userService.getUserEntityByUsername(username);

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Record does not belong to user");
        }

        record.setAmount(request.getAmount());
        record.setType(request.getType().toUpperCase());
        record.setCategory(request.getCategory());
        record.setRecordDate(request.getRecordDate());
        record.setNotes(request.getNotes());

        record = recordRepository.save(record);
        log.info("Financial record updated: id={}, user={}", record.getId(), username);
        return mapToResponse(record);
    }

    public void deleteRecord(Long id, String username) {
        User user = userService.getUserEntityByUsername(username);
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Record does not belong to user");
        }

        record.setIsDeleted(true);
        recordRepository.save(record);
        log.info("Financial record deleted (soft): id={}, user={}", record.getId(), username);
    }

    private void validateRecordType(String type) {
        if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSE")) {
            throw new RuntimeException("Invalid type: " + type + ". Must be INCOME or EXPENSE");
        }
    }

    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return FinancialRecordResponse.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .recordDate(record.getRecordDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
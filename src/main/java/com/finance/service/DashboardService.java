package com.finance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.dto.DashboardSummaryResponse;
import com.finance.dto.FinancialRecordResponse;
import com.finance.entity.User;
import com.finance.repository.FinancialRecordRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j

public class DashboardService {
    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FinancialRecordService recordService;

    public DashboardSummaryResponse getDashboardSummary(String username) {
        User user = userService.getUserEntityByUsername(username);

        BigDecimal totalIncome = recordRepository.getTotalIncome(user);
        BigDecimal totalExpense = recordRepository.getTotalExpense(user);
        
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        List<Object[]> categoryResults = recordRepository.getCategoryTotals(user);
        for (Object[] result : categoryResults) {
            String category = (String) result[0];
            BigDecimal total = (BigDecimal) result[1];
            categoryTotals.put(category, total);
        }

        List<FinancialRecordResponse> recentRecords = recordRepository.getRecentRecords(user).stream()
                .map(record -> FinancialRecordResponse.builder()
                        .id(record.getId())
                        .userId(record.getUser().getId())
                        .amount(record.getAmount())
                        .type(record.getType())
                        .category(record.getCategory())
                        .recordDate(record.getRecordDate())
                        .notes(record.getNotes())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        Integer totalRecords = (int) recordRepository.findByUserAndIsDeletedFalseOrderByRecordDateDesc(user).size();

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .recentRecords(recentRecords)
                .totalRecords(totalRecords)
                .build();
    }
}
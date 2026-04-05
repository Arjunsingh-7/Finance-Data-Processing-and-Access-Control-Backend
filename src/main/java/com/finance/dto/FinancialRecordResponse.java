package com.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordResponse {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDate recordDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
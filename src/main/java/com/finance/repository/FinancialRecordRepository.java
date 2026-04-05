package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinancialRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.finance.entity.User;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    List<FinancialRecord> findByUserAndIsDeletedFalseOrderByRecordDateDesc(User user);
    
    List<FinancialRecord> findByUserAndTypeAndIsDeletedFalse(User user, String type);
    
    List<FinancialRecord> findByUserAndCategoryAndIsDeletedFalse(User user, String category);
    
    List<FinancialRecord> findByUserAndRecordDateBetweenAndIsDeletedFalse(
        User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.user = :user AND f.type = 'INCOME' AND f.isDeleted = false")
    BigDecimal getTotalIncome(@Param("user") User user);
    
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.user = :user AND f.type = 'EXPENSE' AND f.isDeleted = false")
    BigDecimal getTotalExpense(@Param("user") User user);
    
    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f WHERE f.user = :user AND f.isDeleted = false GROUP BY f.category")
    List<Object[]> getCategoryTotals(@Param("user") User user);
    
    @Query("SELECT f FROM FinancialRecord f WHERE f.user = :user AND f.isDeleted = false ORDER BY f.recordDate DESC LIMIT 10")
    List<FinancialRecord> getRecentRecords(@Param("user") User user);
}
package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.CategoryReportResponse;
import com.expensetracker.expensetracker.dto.MonthlyReportResponse;
import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.ExpenseRepository;
import com.expensetracker.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    private User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }   

    // Return total spending per month for the logged in user
    // @Query in ExpenseRepository does the heavy lifting. We just map the raw Object[] to the DTOs
    public List<MonthlyReportResponse> getMonthlyReport(String email){
        
        User user = getUser(email);

        // sumByMonth returns List<Object[]>, each Object[] having 2 elements: [0]->Truncated month & [1]> SUM as BigDecimal
        return expenseRepository.sumByMonth(user.getId())
                                    .stream()
                                    .map(row -> new MonthlyReportResponse(
                                        ((java.time.Instant) row[0])
                                            .atZone(java.time.ZoneId.of("Asia/Kolkata"))
                                            .toLocalDate()
                                            .withDayOfMonth(1)
                                            .toString()
                                            .substring(0, 7), 
                                        (BigDecimal) row[1])).toList();

    }

    // Returns total spending per category for logged in user
    public List<CategoryReportResponse> getCategoryReport(String email) {
        User user = getUser(email);

        // sumByCategory returns List<Object[]>. [0] = category string e.g. "FOOD" & [1] = SUM(amount) as BigDecimal
        return expenseRepository.sumByCategory(user.getId())
                .stream()
                .map(row -> new CategoryReportResponse(
                        (Category) row[0],  // convert string back to enum
                        (BigDecimal) row[1]
                ))
                .toList();
    }

}

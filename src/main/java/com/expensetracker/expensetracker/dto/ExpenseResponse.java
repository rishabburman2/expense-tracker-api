package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Expense;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpenseResponse {
    
    private String id;
    private String title;
    private BigDecimal amount;
    private Category category;
    private LocalDate expenseDate;
    private String note;
    private LocalDateTime createdAt;

    // We only expose userId - not the full User object
    private String userId;

    //Static factory method will convert Expense entity into this DTO. Keeps mapping logic inside DTO
    public static ExpenseResponse from(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setTitle(expense.getTitle());
        response.setAmount(expense.getAmount());
        response.setCategory(expense.getCategory());
        response.setExpenseDate(expense.getExpenseDate());
        response.setNote(expense.getNote());
        response.setCreatedAt(expense.getCreatedAt());
        response.setUserId(expense.getUser().getId());
        return response;
    }

}

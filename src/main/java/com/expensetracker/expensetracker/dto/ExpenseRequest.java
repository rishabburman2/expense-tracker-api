package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    @NotBlank(message = "Title is Required")
    private String title;

    @NotNull(message = "Expense Amount is Required")
    @Positive(message = "Expense amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    private String note;
    
}

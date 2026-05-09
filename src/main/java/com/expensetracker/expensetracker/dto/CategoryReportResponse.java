package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoryReportResponse {
    private Category category;
    private BigDecimal total;
}

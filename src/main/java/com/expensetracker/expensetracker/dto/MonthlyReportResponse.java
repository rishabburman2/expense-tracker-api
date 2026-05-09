package com.expensetracker.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyReportResponse {
    // Month format -> YYYY-MM
    private String month;
    private BigDecimal total;
}

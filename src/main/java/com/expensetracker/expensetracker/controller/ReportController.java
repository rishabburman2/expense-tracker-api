package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.CategoryReportResponse;
import com.expensetracker.expensetracker.dto.MonthlyReportResponse;
import com.expensetracker.expensetracker.service.ReportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;

    // GET /api/reports/monthly
    // Returns total spending grouped by month, most recent first
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyReportResponse>> getMonthlyReport(
        @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.ok(reportService.getMonthlyReport(userDetails.getUsername()));
    }

    // GET /api/reports/category
    @GetMapping("/category")
    public ResponseEntity<List<CategoryReportResponse>> getCategoryReport(
        @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.ok(reportService.getCategoryReport(userDetails.getUsername()));
    }

}

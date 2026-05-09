package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.ExpenseResponse;
import com.expensetracker.expensetracker.dto.ExpenseRequest;
import com.expensetracker.expensetracker.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    
    private final ExpenseService expenseService;

    // @AuthenticationPrincipal injects the currently logged in user directly as a method parameter
    // Spring reads it from SecurityContext so we dont have to call SecurityContextHolder every time

    // POST /api/expenses - create a new expense
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(
        @Valid @RequestBody ExpenseRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.ok(ExpenseResponse.from(expenseService.create(request, userDetails.getUsername())));
    }

    // GET /api/expenses - retrieve expenses for logged in user
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAll(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(
            expenseService.getAll(userDetails.getUsername())
                        .stream()
                        .map(ExpenseResponse::from)
                        .toList());
    }

    // GET /api/expenses/{id} - get single expense by id
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(
        @PathVariable String id,
        @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.ok(ExpenseResponse.from(expenseService.getById(id, userDetails.getUsername())));
    }

    // PUT /api/expenses/{id} - update an expense
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ExpenseResponse.from(
                    expenseService.update(id, request, userDetails.getUsername())));
    }

    // DELETE /api/expenses/{id} - delete an expense
    // ResponseEntity<Void> means we return no body - just a 200 status
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable String id,
        @AuthenticationPrincipal UserDetails userDetails
    ){
        expenseService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}

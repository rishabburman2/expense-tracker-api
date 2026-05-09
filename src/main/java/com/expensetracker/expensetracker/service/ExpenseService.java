package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.ExpenseRequest;
import com.expensetracker.expensetracker.entity.Expense;
import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.ExpenseRepository;
import com.expensetracker.expensetracker.repository.UserRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    // Helper to fetch user from DB using email
    private User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found: " + email));
    }

    // Create new expense for logged in user
    // Email comes from SecurityContext in the controller

    public Expense create(ExpenseRequest request, String email){
        User user = getUser(email);

        // Build Expense entity from DTO
        Expense expense = Expense.builder()
                            .user(user)
                            .title(request.getTitle())
                            .amount(request.getAmount())
                            .category(request.getCategory())
                            .expenseDate(request.getExpenseDate())
                            .note(request.getNote())
                            .build();

        return expenseRepository.save(expense);
    }

    // Return all expenses for this user
    public List<Expense> getAll(String email){
        User user = getUser(email);
        return expenseRepository.findByUserId(user.getId());
    }

    // Returns a single expense by ID
    // Verify if expense belongs to this user. Without this check we will have IDOR vulnerability
    public Expense getById(String id, String email){
        User user = getUser(email);
        Expense expense = expenseRepository.findById(id).orElseThrow(()-> new RuntimeException("Expense not found: " + id));

        // Ownership Check
        if(!expense.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Access Denied");
        }

        return expense;
    }

    // Update an Expense
    public Expense update(String id, ExpenseRequest request, String email){
        Expense expense = getById(id, email); // Already checks ownership

        // Update only fields fetched in the request
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setNote(request.getNote());

        // JPA knows its an update since expense is already present
        return expenseRepository.save(expense);
    }

    // Delete an Expense
    public void delete(String id, String email){
        Expense expense = getById(id, email);
        expenseRepository.delete(expense);
    }
}

package com.expensetracker.expensetracker.entity;

import java.math.BigDecimal;            // We use bigDecimal for currency and not float and double (since they have rounding off errors)
import java.time.LocalDate;             // Date of expense
import java.time.LocalDateTime;         // Full timestamp - when the record was created

// JPA imports
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;  // tells JPA how to store an enum in the DB
import jakarta.persistence.FetchType;   // controls WHEN related data is loaded from DB
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;  // defines the foreign key column
import jakarta.persistence.ManyToOne;   // defines the relationship type
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // We ave a ManyToOne relationship type with User since a user can have many expenses

    // FetchType.LAZY means: don't load the User object from DB automatically. Only load the user when you explicitly call expense.getUser()
    @ManyToOne(fetch = FetchType.LAZY)

    // @JoinColumn tells JPA to create a foreign key column called "user_id" in expenses table that references to user table
    // Nullable false means that every expense should be mapped to a user
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2) // upto 10 total digits, 2 digits after decimal point
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private LocalDate expenseDate;

    private String note;
    private LocalDateTime createdAt;


    @PrePersist
    protected void OnCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

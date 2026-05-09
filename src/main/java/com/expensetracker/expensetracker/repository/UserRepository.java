package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.User;


// JPARepository is a spring  data interface providing save(), findById(), findAll(), etc
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // Spring Data reads the method name and auto-generates the SQL:
    // SELECT * FROM users WHERE email = ?
    // You don't write the query — the method name IS the query

    Optional<User> findByEmail(String email);
    
}

package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.AuthResponse;
import com.expensetracker.expensetracker.dto.LoginRequest;
import com.expensetracker.expensetracker.dto.RegisterRequest;
import com.expensetracker.expensetracker.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController = @Controller + @ResponseBody
// @Controller marks it as a Spring MVC controller
// @ResponseBody means return values are serialised to JSON automatically
@RestController

// All endpoints in this controller are prefixed with /api/auth
// So @PostMapping("/register") becomes POST /api/auth/register
@RequestMapping("/api/auth")


@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    // Handles POST /api/auth/register
    // @RequestBody tells Spring to deserialize the JSON body into RegisterRequest
    // @Valid triggers the validation annotations (@NotBlank, @Email etc) on RegisterRequest
    // ResponseEntity lets us control the HTTP status code in the response
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @Valid @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(authService.login(request));
    }

}

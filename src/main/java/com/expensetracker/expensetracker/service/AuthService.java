package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.AuthResponse;
import com.expensetracker.expensetracker.dto.LoginRequest;
import com.expensetracker.expensetracker.dto.RegisterRequest;
import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.UserRepository;
import com.expensetracker.expensetracker.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request){

        // Check if email is already a token
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already registered");
        }

        // Build user entity from RegisterRequest DTO
        // We never store request.getPassword() directly. It will be encrypted
        User user = User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .build();

        // Save to DB - JPA fires INSERT INTO users
        userRepository.save(user);

        // Generate Jwt Token for this user. This will be used by the client for all further requests
        String token = jwtUtil.generateToken(user.getEmail());

        // Return AuthResponse DTO
        return new AuthResponse(token, user.getEmail(), user.getName());
    }

    public AuthResponse login(LoginRequest request){

        // authenticationManager.authenticate() will:
        // 1. Calls UserDetailsServiceImpl.loadUserByUsername(email) to fetch the user
        // 2. Calls passwordEncoder.matches(rawPassword, hashedPassword) to verify
        // If either fails it throws Exception

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );

        // If authentication is successful, we fetch user and build response
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found"));

        // Generate Jwt Token
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getName());

    }

}

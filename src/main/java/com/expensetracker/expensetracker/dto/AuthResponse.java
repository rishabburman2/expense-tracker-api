package com.expensetracker.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// This will be sent back to the client after login or register
@Data
@AllArgsConstructor // This will be created with: new AuthResponse(token, email, name)
public class AuthResponse {


    // The JWT token — client stores this and sends it
    // in every future request as: Authorization: Bearer <token>
    private String token;

    private String email;
    private String name;
}

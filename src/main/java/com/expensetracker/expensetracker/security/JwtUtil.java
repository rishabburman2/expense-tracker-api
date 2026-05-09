package com.expensetracker.expensetracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// @Component makes Spring manage this as a bean so that we can @Autowired it anywhere we need it
@Component
public class JwtUtil {
    
    // Reads jwt Secret from application.yml
    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.expiration}")
    private long expiration;


    // Convert secret to a cryptographic key
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // generate signed JWT token for the given email. email is stored as "subject" claim inside the token
    public String generateToken(String email){
        return Jwts.builder()
                    .subject(email)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();
    }

    // Records all claims stored inside the token. Claims are the payload - subject, expiry, issued at etc
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                    .verifyWith(getSigningKey()) // Verify the signature using our secret
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }


    // Extracts email (subject) from the token. This is how JwtAuthFilter knows who is making the request
    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }


    // True if token is valid and not expired
    public boolean validateToken(String token){
        try{
            extractAllClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}

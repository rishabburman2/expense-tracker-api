package com.expensetracker.expensetracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{
    
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException{

            // Read authorization header from incoming request
            String authHeader = request.getHeader("Authorization");


            // If there is no Auth header or if it does not start with "Bearer " -> pass the request through, will be blocked by SecurityConfig
            if( authHeader == null || !authHeader.startsWith("Bearer ") ){
                filterChain.doFilter(request, response);
                return;
            }

            // Strip "Bearer " prefix to get raw token
            String token = authHeader.substring(7);

            // Extract email from token
            String email = jwtUtil.extractEmail(token);

            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){

                // Load the full user from DB using the email from the token
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Validate token against the loaded user
                if(jwtUtil.validateToken(token)){

                    // Create an auth object
                    // principal = who they are, credentials = null (token already validated), authorities = their roles/ perms
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    // Attach request details to the auth token
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Put authenticated user into SecurityContext
                    // From this point on, any controller can call SecurityContextHolder.getContext().getAuthentication() and get this user

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }

            // Always pass the request to the next filter in the chain
            filterChain.doFilter(request, response);
        }

}

package com.expensetracker.expensetracker.security;

import com.expensetracker.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// @RequiredArgsConstructor generates a constructor for all final fields
// @Service tells that it is a service class
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
            () -> new UsernameNotFoundException("No user found with email: " + email)
        );
    }
}

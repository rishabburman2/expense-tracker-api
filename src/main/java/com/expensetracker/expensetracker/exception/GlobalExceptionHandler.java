package com.expensetracker.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice means: intercept exceptions thrown by any controller and handle them centrally
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Builds a consistent error response map -> status, error, timestamp
    private Map<String, Object> buildError(HttpStatus status, String message){
        Map<String, Object> error = new HashMap<>();
        error.put("status", status.value());
        error.put("error", message);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // Handles RuntimeException. We throw Runtime Exception in AuthService (dupe email, user not found etc) and ExpenseService
    // Returns 400 Bad Request for these cases
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // Handles auth failures - wrong password during login
    // Returns 401 Unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    // Handles @Valid validation failures or request bodies eg missing email, password too short etc
    // Returns 400 with a map of field -> error message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex){

        // Collect all field validation errors in a map
        Map<String, Object> fieldErrors = new HashMap<>();
        for(FieldError fieldError: ex.getBindingResult().getFieldErrors()){
            fieldErrors.put(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                    );
        }

        Map<String, Object> error = buildError(HttpStatus.BAD_REQUEST, "Validation Failed");
        error.put("fields", fieldErrors); 

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Catch-all handles any exception not handled explicitly
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralExceptions(Exception ex){
        return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"));
    }

}

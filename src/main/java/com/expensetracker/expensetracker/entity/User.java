package com.expensetracker.expensetracker.entity;

// --- Java standard library imports ---
import java.time.LocalDateTime;
import java.util.Collection;                // used for getAutorities() method
import java.util.List;                      // used to return an empty list of roles

// --- JPA imports (for DB mapping) ---
import jakarta.persistence.Column;          // customise a column (unique, nullable etc)
import jakarta.persistence.Entity;          // marks this class as a DB table
import jakarta.persistence.GeneratedValue;  // auto-generate the ID value
import jakarta.persistence.GenerationType;  // defines HOW to generate (UUID, sequence etc)
import jakarta.persistence.Id;              // marks which field is the primary key
import jakarta.persistence.PrePersist;      // runs a method automatically before saving to DB
import jakarta.persistence.Table;           // lets you name the DB table explicitly

// --- Lombok imports (removes boilerplate) ---
import lombok.AllArgsConstructor;           // Generates a constructor with all fields as parameter
import lombok.Builder;
import lombok.Data;                         // Generates getters, setters, equals(), hashCode(), toString()
import lombok.NoArgsConstructor;            // Empty constructor for JPA


// --- Spring Security imports ---
// UserDetails is a Spring Security interface
// By implementing it, Spring Security can use User directly for authentication
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// @Entity tells JPA that this class represents a table in DB. If you remove this → Spring will ignore this class completely for DB.
@Entity


@Table(name = "users")

// @Data generates getters, setters, equals(), hashCode(), toString() for each field
// This will automatically generate getEmail(), setEmail(), toString(), equals(), hashCode()
@Data

@NoArgsConstructor

// Useful for testing. Generates public User(String id, String email, String password, ...)
@AllArgsConstructor

// Lets you use the builder pattern: User.builder().email("a@b.com").build(). Hence no need of constructors
@Builder
public class User implements UserDetails{           //Spring Security can use this class for login/ auth
    
    @Id                                             // Marks this as primary key column

    @GeneratedValue(strategy = GenerationType.UUID) // @GeneratedValue tells JPA to auto-generate this value. Will generate a random UUID

    private String id;

    @Column(unique = true, nullable = false)        // @Column lets us add constraints on the DB column.
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    private LocalDateTime createdAt;


    // @PrePersist means run this method automatically before saving a new user to the DB. This way createdAt will akways be set.
    @PrePersist
    protected void OnCreate(){
        this.createdAt = LocalDateTime.now();
    }


    // --- UserDetails interface methods ---
    // Spring Security calls these methods to understand the user's permissions

    // Returns the user's roles/permissions (e.g. ADMIN, USER)
    // We're keeping it simple — returning an empty list means no special roles

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // Lombok automatically generates String getPassword(). We override this explicitly to be clear
    @Override
    public String getPassword(){
        return password;
    }

    // Overriding getUsername() as in our app email -> username
    @Override
    public String getUsername(){
        return email;
    }

    // The following 3 methods control account status
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    // Returns true if this account is active/ enabled
    @Override
    public boolean isEnabled() {
        return true;
    }

}

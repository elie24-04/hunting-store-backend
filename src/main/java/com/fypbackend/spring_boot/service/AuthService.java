package com.fypbackend.spring_boot.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fypbackend.spring_boot.dao.CustomerRepository;
import com.fypbackend.spring_boot.dao.UserAccountRepository;
import com.fypbackend.spring_boot.dto.auth.AuthResponse;
import com.fypbackend.spring_boot.dto.auth.AuthUser;
import com.fypbackend.spring_boot.dto.auth.LoginRequest;
import com.fypbackend.spring_boot.dto.auth.RegisterRequest;
import com.fypbackend.spring_boot.entity.Customer;
import com.fypbackend.spring_boot.entity.UserAccount;
import com.fypbackend.spring_boot.entity.UserRole;
import com.fypbackend.spring_boot.security.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CustomerRepository customerRepository;

    public AuthService(UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService,
            CustomerRepository customerRepository) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        userAccountRepository.findByEmail(normalizedEmail)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
                });

        UserAccount user = new UserAccount();
        user.setEmail(normalizedEmail);
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CLIENT);
        userAccountRepository.save(user);

        NameParts nameParts = splitName(request.getFullName());
        Customer customer = customerRepository.findByEmail(normalizedEmail)
                .orElseGet(Customer::new);
        customer.setFirstName(nameParts.firstName());
        customer.setLastName(nameParts.lastName());
        customer.setEmail(normalizedEmail);
        customerRepository.save(customer);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        UserAccount user = userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    public AuthUser getProfile(String email) {
        String normalizedEmail = normalizeEmail(email);
        UserAccount user = userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new AuthUser(user.getEmail(), user.getFullName(), user.getRole());
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT).trim();
    }

    private AuthResponse buildAuthResponse(UserAccount user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("fullName", user.getFullName());
        claims.put("userId", user.getId());

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(claims, userDetails);

        AuthUser authUser = new AuthUser(user.getEmail(), user.getFullName(), user.getRole());
        return new AuthResponse(token, authUser);
    }

    private NameParts splitName(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isEmpty()) {
            return new NameParts("Client", "");
        }
        String[] pieces = trimmed.split("\\s+", 2);
        String first = pieces[0];
        String last = pieces.length > 1 ? pieces[1] : "";
        return new NameParts(first, last);
    }

    private record NameParts(String firstName, String lastName) {
    }
}

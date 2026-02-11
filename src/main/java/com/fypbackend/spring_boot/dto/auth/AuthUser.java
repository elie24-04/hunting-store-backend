package com.fypbackend.spring_boot.dto.auth;

import com.fypbackend.spring_boot.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthUser {
    private String email;
    private String fullName;
    private UserRole role;
}


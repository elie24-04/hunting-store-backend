package com.fypbackend.spring_boot.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Email
    @NotBlank
    @JsonAlias("username")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}

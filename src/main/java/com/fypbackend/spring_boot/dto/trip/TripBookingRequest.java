package com.fypbackend.spring_boot.dto.trip;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TripBookingRequest {

    @NotBlank
    @JsonAlias("full_name")
    private String fullName;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @Min(1)
    @JsonAlias("people_count")
    private int peopleCount;

    @NotNull
    @JsonAlias("preferred_date")
    private LocalDate preferredDate;

    @JsonAlias("experience_level")
    private String experienceLevel;

    private String notes;
}

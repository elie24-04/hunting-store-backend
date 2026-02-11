package com.fypbackend.spring_boot.dto.message;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateNoteRequest {

    @Size(max = 5000)
    private String adminNote;
}

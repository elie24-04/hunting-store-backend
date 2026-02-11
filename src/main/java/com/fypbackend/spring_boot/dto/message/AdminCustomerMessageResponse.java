package com.fypbackend.spring_boot.dto.message;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fypbackend.spring_boot.entity.MessageStatus;
import com.fypbackend.spring_boot.entity.MessageType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminCustomerMessageResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String subject;
    private String message;
    private MessageType type;
    private MessageStatus status;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

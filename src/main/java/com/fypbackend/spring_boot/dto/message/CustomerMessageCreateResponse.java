package com.fypbackend.spring_boot.dto.message;

import java.util.UUID;

import com.fypbackend.spring_boot.entity.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerMessageCreateResponse {
    private UUID id;
    private MessageStatus status;
}

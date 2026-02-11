package com.fypbackend.spring_boot.dto.message;

import com.fypbackend.spring_boot.entity.MessageStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateStatusRequest {

    @NotNull
    private MessageStatus status;
}

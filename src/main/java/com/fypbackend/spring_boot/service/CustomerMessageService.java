package com.fypbackend.spring_boot.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fypbackend.spring_boot.dto.message.AdminCustomerMessageResponse;
import com.fypbackend.spring_boot.dto.message.CustomerMessageCreateRequest;
import com.fypbackend.spring_boot.entity.MessageStatus;
import com.fypbackend.spring_boot.entity.MessageType;

public interface CustomerMessageService {

    UUID createMessage(CustomerMessageCreateRequest request);

    Page<AdminCustomerMessageResponse> adminListMessages(
            MessageStatus status,
            MessageType type,
            String q,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Pageable pageable);

    AdminCustomerMessageResponse updateStatus(UUID id, MessageStatus status);

    AdminCustomerMessageResponse updateAdminNote(UUID id, String note);

    AdminCustomerMessageResponse getById(UUID id);
}

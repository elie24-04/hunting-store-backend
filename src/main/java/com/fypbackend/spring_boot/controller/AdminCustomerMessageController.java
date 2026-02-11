package com.fypbackend.spring_boot.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fypbackend.spring_boot.dto.message.AdminCustomerMessageResponse;
import com.fypbackend.spring_boot.dto.message.AdminUpdateNoteRequest;
import com.fypbackend.spring_boot.dto.message.AdminUpdateStatusRequest;
import com.fypbackend.spring_boot.entity.MessageStatus;
import com.fypbackend.spring_boot.entity.MessageType;
import com.fypbackend.spring_boot.service.CustomerMessageService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/messages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerMessageController {

    private final CustomerMessageService customerMessageService;

    public AdminCustomerMessageController(CustomerMessageService customerMessageService) {
        this.customerMessageService = customerMessageService;
    }

    @GetMapping
    public Page<AdminCustomerMessageResponse> listMessages(
            @RequestParam(required = false) MessageStatus status,
            @RequestParam(required = false) MessageType type,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return customerMessageService.adminListMessages(status, type, q, dateFrom, dateTo, pageable);
    }

    @GetMapping("/{id}")
    public AdminCustomerMessageResponse getById(@PathVariable UUID id) {
        return customerMessageService.getById(id);
    }

    @PutMapping("/{id}/status")
    public AdminCustomerMessageResponse updateStatus(@PathVariable UUID id,
                                                     @Valid @RequestBody AdminUpdateStatusRequest request) {
        return customerMessageService.updateStatus(id, request.getStatus());
    }

    @PutMapping("/{id}/note")
    public AdminCustomerMessageResponse updateNote(@PathVariable UUID id,
                                                   @Valid @RequestBody AdminUpdateNoteRequest request) {
        return customerMessageService.updateAdminNote(id, request.getAdminNote());
    }
}

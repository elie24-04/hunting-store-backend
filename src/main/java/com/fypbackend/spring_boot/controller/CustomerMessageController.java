package com.fypbackend.spring_boot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fypbackend.spring_boot.dto.message.CustomerMessageCreateRequest;
import com.fypbackend.spring_boot.dto.message.CustomerMessageCreateResponse;
import com.fypbackend.spring_boot.entity.MessageStatus;
import com.fypbackend.spring_boot.service.CustomerMessageService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/messages")
public class CustomerMessageController {

    private final CustomerMessageService customerMessageService;

    public CustomerMessageController(CustomerMessageService customerMessageService) {
        this.customerMessageService = customerMessageService;
    }

    @PostMapping
    public ResponseEntity<CustomerMessageCreateResponse> createMessage(@Valid @RequestBody CustomerMessageCreateRequest request) {
        CustomerMessageCreateResponse response = new CustomerMessageCreateResponse(
                customerMessageService.createMessage(request),
                MessageStatus.NEW);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

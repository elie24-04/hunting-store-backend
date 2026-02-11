package com.fypbackend.spring_boot.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOrderResponse {
    private Long id;
    private String status;
    private String trackingNumber;
    private BigDecimal totalPrice;
    private int totalQuantity;
    private LocalDateTime dateCreated;
    private String customerName;
    private String customerEmail;
    private List<AdminOrderItemDto> items;
}


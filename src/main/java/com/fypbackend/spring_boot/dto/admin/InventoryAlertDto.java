package com.fypbackend.spring_boot.dto.admin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryAlertDto {
    private Long productId;
    private String productName;
    private int unitsInStock;
    private BigDecimal unitPrice;
    private boolean active;
}

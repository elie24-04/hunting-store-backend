package com.fypbackend.spring_boot.dto.admin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSellingProductDto {
    private Long productId;
    private String productName;
    private Long unitsSold;
    private BigDecimal revenue;
}

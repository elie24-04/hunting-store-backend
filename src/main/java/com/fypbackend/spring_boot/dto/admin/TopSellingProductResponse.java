package com.fypbackend.spring_boot.dto.admin;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSellingProductResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private int limit;
    private List<TopSellingProductDto> products;
}

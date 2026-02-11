package com.fypbackend.spring_boot.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesTrendResponse {
    private String interval;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> labels;
    private List<BigDecimal> totals;
    private String currency;
}

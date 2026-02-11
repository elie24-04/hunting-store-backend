package com.fypbackend.spring_boot.dto.admin;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatisticsResponse {
    private String interval;
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalOrders;
    private List<String> labels;
    private List<Long> counts;
}

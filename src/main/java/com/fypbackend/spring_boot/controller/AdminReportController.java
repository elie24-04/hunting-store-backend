package com.fypbackend.spring_boot.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fypbackend.spring_boot.dto.admin.InventoryAlertResponse;
import com.fypbackend.spring_boot.dto.admin.KpiResponse;
import com.fypbackend.spring_boot.dto.admin.OrderStatisticsResponse;
import com.fypbackend.spring_boot.dto.admin.SalesTrendResponse;
import com.fypbackend.spring_boot.dto.admin.TopSellingProductResponse;
import com.fypbackend.spring_boot.service.ReportService;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping({"/sales/trend", "/sales-trend"})
    public SalesTrendResponse getSalesTrend(
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return reportService.buildSalesTrend(interval, start, end);
    }

    @GetMapping("/orders/statistics")
    public OrderStatisticsResponse getOrderStatistics(
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return reportService.buildOrderStatistics(interval, start, end);
    }

    @GetMapping("/products/top")
    public TopSellingProductResponse getTopProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return reportService.buildTopSellingProducts(start, end, limit);
    }

    @GetMapping("/products/alerts")
    public InventoryAlertResponse getInventoryAlerts(
            @RequestParam(required = false, defaultValue = "10") Integer threshold) {
        return reportService.buildInventoryAlerts(threshold);
    }

    @GetMapping("/kpis")
    public KpiResponse getKpis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return reportService.buildKpis(start, end);
    }
}

package com.fypbackend.spring_boot.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fypbackend.spring_boot.dto.admin.AdminOrderResponse;
import com.fypbackend.spring_boot.dto.admin.OrderStatusUpdateRequest;
import com.fypbackend.spring_boot.service.AdminOrderService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public Page<AdminOrderResponse> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        return adminOrderService.listOrders(page, size, status);
    }

    @PatchMapping("/{orderId}")
    public AdminOrderResponse updateStatus(@PathVariable Long orderId, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return adminOrderService.updateOrderStatus(orderId, request.getStatus());
    }
}

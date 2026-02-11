package com.fypbackend.spring_boot.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.fypbackend.spring_boot.dao.OrderRepository;
import com.fypbackend.spring_boot.dto.admin.AdminOrderItemDto;
import com.fypbackend.spring_boot.dto.admin.AdminOrderResponse;
import com.fypbackend.spring_boot.entity.Customer;
import com.fypbackend.spring_boot.entity.Order;
import com.fypbackend.spring_boot.entity.OrderItem;
import com.fypbackend.spring_boot.entity.OrderStatus;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public AdminOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminOrderResponse> listOrders(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated"));

        Page<Order> orders;
        if (StringUtils.hasText(status)) {
            orders = orderRepository.findByStatusIgnoreCase(status, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(this::toResponse);
    }

    @Transactional
    public AdminOrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.fromValue(status);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        order.setStatus(orderStatus.name());
        orderRepository.save(order);

        return toResponse(order);
    }

    private AdminOrderResponse toResponse(Order order) {
        Customer customer = order.getCustomer();
        String customerName = "";
        String customerEmail = null;
        if (customer != null) {
            String first = customer.getFirstName() == null ? "" : customer.getFirstName();
            String last = customer.getLastName() == null ? "" : customer.getLastName();
            customerName = String.format("%s %s", first, last).trim();
            customerEmail = customer.getEmail();
        }

        List<AdminOrderItemDto> items = order.getOrderItems()
                .stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toList());

        return new AdminOrderResponse(
                order.getId(),
                order.getStatus(),
                order.getOrderTrackingNumber(),
                order.getTotalPrice(),
                order.getTotalQuantity(),
                order.getDateCreated(),
                customerName,
                customerEmail,
                items);
    }

    private AdminOrderItemDto toOrderItemDto(OrderItem orderItem) {
        BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());
        BigDecimal lineTotal = orderItem.getUnitPrice() == null
                ? BigDecimal.ZERO
                : orderItem.getUnitPrice().multiply(quantity);
        return new AdminOrderItemDto(
                orderItem.getProductId(),
                orderItem.getImageUrl(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                lineTotal);
    }
}

package com.fypbackend.spring_boot.entity;

public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    CANCELLED;

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unsupported order status: " + value);
    }
}


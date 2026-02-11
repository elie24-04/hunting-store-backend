package com.fypbackend.spring_boot.dto;

import java.util.Set;

import com.fypbackend.spring_boot.entity.Address;
import com.fypbackend.spring_boot.entity.Customer;
import com.fypbackend.spring_boot.entity.Order;
import com.fypbackend.spring_boot.entity.OrderItem;

import lombok.Data;

@Data
public class Purchase {

    private Customer customer;
    private Address shippingAddress;
    private Address billingAddress;
    private Order order;
    private Set<OrderItem> orderItems;

}

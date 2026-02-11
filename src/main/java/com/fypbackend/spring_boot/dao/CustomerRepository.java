package com.fypbackend.spring_boot.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fypbackend.spring_boot.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

}

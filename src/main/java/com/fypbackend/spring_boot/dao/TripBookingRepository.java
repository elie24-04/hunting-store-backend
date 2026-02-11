package com.fypbackend.spring_boot.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fypbackend.spring_boot.entity.TripBooking;

public interface TripBookingRepository extends JpaRepository<TripBooking, UUID> {
}

package com.fypbackend.spring_boot.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fypbackend.spring_boot.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, String> {
}

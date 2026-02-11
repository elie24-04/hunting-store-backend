package com.fypbackend.spring_boot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fypbackend.spring_boot.dao.TripRepository;
import com.fypbackend.spring_boot.dto.trip.TripBookingRequest;
import com.fypbackend.spring_boot.dto.trip.TripBookingResponse;
import com.fypbackend.spring_boot.entity.Trip;
import com.fypbackend.spring_boot.service.TripBookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripRepository tripRepository;
    private final TripBookingService tripBookingService;

    public TripController(TripRepository tripRepository, TripBookingService tripBookingService) {
        this.tripRepository = tripRepository;
        this.tripBookingService = tripBookingService;
    }

    @GetMapping
    public List<Trip> getTrips() {
        return tripRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTrip(@PathVariable String id) {
        return tripRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/bookings")
    public TripBookingResponse createBooking(@PathVariable String id,
                                             @Valid @RequestBody TripBookingRequest request) {
        return tripBookingService.createBooking(id, request);
    }
}

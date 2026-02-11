package com.fypbackend.spring_boot.service;

import com.fypbackend.spring_boot.dto.trip.TripBookingRequest;
import com.fypbackend.spring_boot.dto.trip.TripBookingResponse;

public interface TripBookingService {
    TripBookingResponse createBooking(String tripId, TripBookingRequest request);
}

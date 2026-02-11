package com.fypbackend.spring_boot.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fypbackend.spring_boot.dao.TripBookingRepository;
import com.fypbackend.spring_boot.dao.TripRepository;
import com.fypbackend.spring_boot.dto.trip.TripBookingRequest;
import com.fypbackend.spring_boot.dto.trip.TripBookingResponse;
import com.fypbackend.spring_boot.entity.Trip;
import com.fypbackend.spring_boot.entity.TripBooking;
import com.fypbackend.spring_boot.entity.TripBookingStatus;

import jakarta.transaction.Transactional;

@Service
public class TripBookingServiceImpl implements TripBookingService {

    private static final DateTimeFormatter REF_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final TripBookingRepository tripBookingRepository;
    private final TripRepository tripRepository;

    public TripBookingServiceImpl(TripBookingRepository tripBookingRepository, TripRepository tripRepository) {
        this.tripBookingRepository = tripBookingRepository;
        this.tripRepository = tripRepository;
    }

    @Override
    @Transactional
    public TripBookingResponse createBooking(String tripId, TripBookingRequest request) {
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null && tripRepository.count() > 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }

        if (trip != null) {
            validateAgainstTrip(trip, request);
        }

        TripBooking booking = new TripBooking();
        booking.setTripId(tripId);
        booking.setFullName(request.getFullName());
        booking.setEmail(request.getEmail());
        booking.setPhone(request.getPhone());
        booking.setPeopleCount(request.getPeopleCount());
        booking.setPreferredDate(request.getPreferredDate());
        booking.setExperienceLevel(request.getExperienceLevel());
        booking.setNotes(request.getNotes());
        booking.setStatus(TripBookingStatus.PENDING);
        booking.setReferenceCode(generateReferenceCode(request.getPreferredDate()));

        TripBooking saved = tripBookingRepository.save(booking);

        return new TripBookingResponse(
                saved.getId().toString(),
                saved.getReferenceCode(),
                saved.getStatus().name(),
                "Booking request received");
    }

    private void validateAgainstTrip(Trip trip, TripBookingRequest request) {
        if (trip.getMaxPeople() != null && request.getPeopleCount() > trip.getMaxPeople()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "people_count must be <= maxPeople");
        }

        LocalDate preferredDate = request.getPreferredDate();
        if (preferredDate == null) {
            return;
        }

        if (trip.getAvailableStartDate() != null && preferredDate.isBefore(trip.getAvailableStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "preferred_date is before available range");
        }

        if (trip.getAvailableEndDate() != null && preferredDate.isAfter(trip.getAvailableEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "preferred_date is after available range");
        }
    }

    private String generateReferenceCode(LocalDate preferredDate) {
        LocalDate baseDate = preferredDate != null ? preferredDate : LocalDate.now();
        int suffix = ThreadLocalRandom.current().nextInt(10000);
        return "TRIP-" + baseDate.format(REF_DATE) + "-" + String.format("%04d", suffix);
    }
}

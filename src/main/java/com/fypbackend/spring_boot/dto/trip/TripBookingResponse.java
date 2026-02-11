package com.fypbackend.spring_boot.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripBookingResponse {
    private String bookingId;
    private String referenceCode;
    private String status;
    private String message;
}

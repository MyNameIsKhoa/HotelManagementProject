package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Booking;

import java.time.LocalDate;

public interface BookingService {
    Booking createBooking(
            Long userId,
            Long roomTypeId,
            LocalDate checkinDate,
            LocalDate checkoutDate
    );
    Booking getById(Long id);
}

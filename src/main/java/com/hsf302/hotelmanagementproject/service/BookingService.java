package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking createBooking(
            Long userId,
            Long roomTypeId,
            LocalDateTime checkinDate,
            LocalDateTime checkoutDate
    );
    Booking getById(Long id);
    public List<Booking> getPendingBookings();

    public void confirmBooking(Long bookingId);
    public List<Booking> getBookingsByUser(Long userId);

}

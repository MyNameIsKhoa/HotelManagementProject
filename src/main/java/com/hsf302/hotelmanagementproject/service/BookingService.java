package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.DTO.CreateBookingRequest;
import com.hsf302.hotelmanagementproject.entity.Booking;

import java.util.List;
import java.util.Map;

public interface BookingService {


    Map<String, Object> createBooking (Long userId, CreateBookingRequest request);
    public Booking confirmDepositByOrderCode(Long orderCode);


    Booking assignRoom(Long bookingId, Long roomId);
    void handlePayOSWebhook(Long orderCode);
    public List<Booking> getBookingsByUser(Long userId);

    Booking getById(Long id);

    void confirmBooking(Long bookingId);

    void cancelBooking(Long bookingId);
}

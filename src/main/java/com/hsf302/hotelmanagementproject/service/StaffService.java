package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.enums.PaymentMethod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StaffService {
    List<Booking> getAllBookings();

    List<Booking> searchByCheckinDate(LocalDate date);

    List<Booking> getCheckinToday();

    List<Booking> getTodayCheckouts();

    Booking getBooking(Long id);

    void assignRoom(Long bookingId, Long roomId);

    void checkIn(Long bookingId);

    void checkOut(Long bookingId);

    List<Room> getAvailableRoomsByRoomType(Long roomTypeId);
    Map<String, Object> checkoutWithPayment(Long bookingId, PaymentMethod method);

}

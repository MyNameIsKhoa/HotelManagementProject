package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.Room;

import java.util.List;

public interface StaffService {
    List<Booking> getAllBookings();
    Booking getById(Long id);
    List<Booking> getTodayCheckins();
    List<Booking> getTodayCheckouts();
    void assignRoom(Long bookingId, Long roomId);
    void checkIn(Long bookingId);
    void pay(Long bookingId);
    void checkOut(Long bookingId);
    List<Room> getAvailableRoomsByRoomType(Long roomTypeId);

}

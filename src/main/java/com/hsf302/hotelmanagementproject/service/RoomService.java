package com.hsf302.hotelmanagementproject.service;

import java.util.List;
import java.util.Map;

public interface RoomService {
    List<Map<String, Object>> getRoomsStatusForBooking(Long bookingId);
}

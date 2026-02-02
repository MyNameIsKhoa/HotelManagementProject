package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Hotel;
import com.hsf302.hotelmanagementproject.entity.RoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SearchService {
    Map<Hotel, List<Object[]>> searchAvailableRooms(
            LocalDateTime checkin,
            LocalDateTime checkout
    );
    int countAvailableRooms(
            Long roomTypeId,
            LocalDateTime checkin,
            LocalDateTime checkout
    );
    RoomType getRoomType(Long id);
}

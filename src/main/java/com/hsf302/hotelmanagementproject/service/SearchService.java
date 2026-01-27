package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Hotel;
import com.hsf302.hotelmanagementproject.entity.RoomImage;
import com.hsf302.hotelmanagementproject.entity.RoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SearchService {

    // ===== SEARCH =====
    Map<Hotel, List<Object[]>> searchAvailableRooms(
            LocalDateTime checkin,
            LocalDateTime checkout
    );

    // ===== ROOM DETAIL =====
    RoomType getRoomType(Long roomTypeId);

    int countAvailableRooms(
            Long roomTypeId,
            LocalDateTime checkin,
            LocalDateTime checkout
    );
    RoomImage addedRoomImage(RoomImage roomImage);

    RoomImage getThumbnail(RoomType roomType);

    List<RoomImage> getRoomImages(RoomType roomType);
}

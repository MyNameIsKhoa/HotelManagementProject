package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.RoomType;

public interface RoomTypeService {
    RoomType createRoomType(RoomType roomType);
    RoomType getRoomTypeById(Long id);
}

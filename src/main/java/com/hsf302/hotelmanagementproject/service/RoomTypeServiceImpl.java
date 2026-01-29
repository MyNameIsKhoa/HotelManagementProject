package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeServiceImpl  implements RoomTypeService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Override
    public RoomType createRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    @Override
    public RoomType getRoomTypeById(Long id) {
        return roomTypeRepository.findById(id).orElse(null);
    }
}

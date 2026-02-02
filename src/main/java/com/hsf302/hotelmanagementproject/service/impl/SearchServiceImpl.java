package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.entity.Hotel;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.repository.HotelRepository;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import com.hsf302.hotelmanagementproject.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Override
    public Map<Hotel, List<Object[]>>  searchAvailableRooms(
            LocalDateTime checkin,
            LocalDateTime checkout
    ) {
        Map<Hotel, List<Object[]>> result = new LinkedHashMap<>();

        List<Hotel> hotels = hotelRepository.findAll();

        for (Hotel hotel : hotels) {
            List<Object[]> roomTypes =
                    roomTypeRepository.findAvailableRoomTypesByHotel(
                            hotel.getHotelId(), checkin, checkout);

            if (!roomTypes.isEmpty()) {
                result.put(hotel, roomTypes);
            }
        }
        return result;
    }
    @Override
    public int countAvailableRooms(
            Long roomTypeId,
            LocalDateTime checkin,
            LocalDateTime checkout
    ) {
        return roomTypeRepository.countAvailableRooms(
                roomTypeId, checkin, checkout
        );
    }

    @Override
    public RoomType getRoomType(Long id) {
        return roomTypeRepository.findById(id).orElse(null);
    }
}
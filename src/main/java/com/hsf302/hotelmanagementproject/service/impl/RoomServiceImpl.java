package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.repository.BookingRepository;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<Map<String, Object>> getRoomsStatusForBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        List<Room> rooms =
                roomRepository.findByRoomType_RoomTypeId(
                        booking.getRoomType().getRoomTypeId()
                );

        List<Long> occupiedRoomIds =
                roomRepository.findOccupiedRoomIds(
                        booking.getCheckinDate(),
                        booking.getCheckoutDate()
                );

        List<Map<String, Object>> result = new ArrayList<>();

        for (Room room : rooms) {

            Map<String, Object> map = new HashMap<>();
            map.put("id", room.getRoomId());
            map.put("number", room.getRoomNumber());

            boolean occupied = occupiedRoomIds.contains(room.getRoomId());

            map.put("status", occupied ? "OCCUPIED" : "AVAILABLE");

            result.add(map);
        }

        return result;
    }
}

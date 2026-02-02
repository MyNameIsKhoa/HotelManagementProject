package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * API trả về danh sách phòng + trạng thái theo booking
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRoomsStatusByBooking(
            @RequestParam Long bookingId
    ) {

        return ResponseEntity.ok(
                roomService.getRoomsStatusForBooking(bookingId)
        );
    }
}
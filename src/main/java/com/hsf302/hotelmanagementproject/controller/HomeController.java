package com.hsf302.hotelmanagementproject.controller;



import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {
// test//
    /**
     * Trang chủ
     * GET /
     */
    private final RoomTypeRepository roomTypeRepository;
    public HomeController(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String error) {
        List<RoomType> featuredRooms = roomTypeRepository.findTop3ByOrderByRoomTypeIdAsc();
        LocalDateTime checkin = LocalDateTime.now().plusHours(1);
        LocalDateTime checkout = checkin.plusDays(1);
        model.addAttribute("checkinDate", checkin);
        model.addAttribute("checkoutDate", checkout);
        model.addAttribute("featuredRooms", featuredRooms);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "home/index";
    }



}

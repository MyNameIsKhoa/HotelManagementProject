package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.RoomImage;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.repository.RoomImageRepository;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import com.hsf302.hotelmanagementproject.service.SearchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller

public class SearchController {
    @Autowired
    private RoomImageRepository roomImageRepository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "checkinDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkin,

            @RequestParam(value = "checkoutDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkout,

            Model model
    ) {


        try {
            if (checkin != null && checkout != null) {
                model.addAttribute("results",
                        searchService.searchAvailableRooms(checkin, checkout));
                model.addAttribute("checkinDate", checkin);
                model.addAttribute("checkoutDate", checkout);
            } else {
                model.addAttribute("results", searchService.getAllRoomTypes());
            }

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "home/index";
        }
        return "search/search";
    }

    @GetMapping("/room/{id}")
    public String roomDetail(
            @PathVariable("id") Long id,

            @RequestParam(value = "checkinDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkin,

            @RequestParam(value = "checkoutDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkout,
            HttpSession session,
            Model model
    ) {
        RoomType roomType = roomTypeRepository.findById(id).orElseThrow();

        int availableRooms = 0;
        if(checkin != null && checkout != null) {
            availableRooms = searchService.countAvailableRooms(id, checkin, checkout);
        }

        // 👉 LẤY ẢNH

        List<RoomImage> images =
                roomImageRepository.findByRoomTypeAndIsThumbnailFalse(roomType);

        RoomImage thumbnail =
                roomImageRepository.findFirstByRoomTypeAndIsThumbnailTrue(roomType);

        model.addAttribute("roomType", roomType);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("checkinDate", checkin);
        model.addAttribute("checkoutDate", checkout);
        model.addAttribute("currentUser", session.getAttribute("currentUser"));

        model.addAttribute("images", images);
        model.addAttribute("thumbnail", thumbnail);

        return "search/room_detail";
    }
}
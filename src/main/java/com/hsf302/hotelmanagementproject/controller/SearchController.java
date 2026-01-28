package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.RoomImage;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.repository.RoomImageRepository;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import com.hsf302.hotelmanagementproject.service.SearchService;
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
            @RequestParam("checkinDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkin,

            @RequestParam("checkoutDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkout,

            Model model
    ) {
        model.addAttribute("results",
                searchService.searchAvailableRooms(checkin, checkout));
        model.addAttribute("checkinDate", checkin);
        model.addAttribute("checkoutDate", checkout);
        return "search/search";
    }

    @GetMapping("/room/{id}")
    public String roomDetail(
            @PathVariable("id") Long id,

            @RequestParam("checkinDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkin,

            @RequestParam("checkoutDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkout,

            Model model
    ) {
        RoomType roomType = roomTypeRepository.findById(id).orElseThrow();

        int availableRooms =
                searchService.countAvailableRooms(id, checkin, checkout);
        int a= 0;
        // 👉 LẤY ẢNH

        List<RoomImage> images =
                roomImageRepository.findByRoomTypeAndIsThumbnailFalse(roomType);

        RoomImage thumbnail =
                roomImageRepository.findFirstByRoomTypeAndIsThumbnailTrue(roomType);

        model.addAttribute("roomType", roomType);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("checkinDate", checkin);
        model.addAttribute("checkoutDate", checkout);

        // 👉 GỬI SANG VIEW
        model.addAttribute("images", images);
        model.addAttribute("thumbnail", thumbnail);

        return "search/room_detail";
    }
}
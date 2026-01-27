package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    // ===== SEARCH PAGE =====
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
        model.addAttribute(
                "results",
                searchService.searchAvailableRooms(checkin, checkout)
        );
        model.addAttribute("checkinDate", checkin);
        model.addAttribute("checkoutDate", checkout);

        return "search/search";
    }

    // ===== ROOM DETAIL =====
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
        RoomType roomType = searchService.getRoomType(id);

        model.addAttribute("roomType", roomType);
        model.addAttribute(
                "availableRooms",
                searchService.countAvailableRooms(id, checkin, checkout)
        );
        model.addAttribute(
                "thumbnail",
                searchService.getThumbnail(roomType)
        );
        model.addAttribute(
                "images",
                searchService.getRoomImages(roomType)
        );
        model.addAttribute("checkinDate", checkin);
        model.addAttribute("checkoutDate", checkout);

        return "search/room_detail";
    }
}

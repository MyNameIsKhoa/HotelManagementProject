package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.Role;
import com.hsf302.hotelmanagementproject.service.StatisticsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/stats")
@RequiredArgsConstructor
public class AdminApiController {

    private final StatisticsService statisticsService;

    @GetMapping("/room-status")
    public ResponseEntity<?> getRoomStatusStats(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(statisticsService.getRoomStatusStats());
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStats(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(statisticsService.getRevenueByRoomTypeStats());
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(statisticsService.getDashboardSummary());
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null && user.getRole() == Role.ADMIN;
    }
}


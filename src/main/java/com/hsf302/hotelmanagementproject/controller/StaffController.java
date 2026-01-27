package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import com.hsf302.hotelmanagementproject.entity.enums.Role;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.service.StaffService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final RoomRepository roomRepository;
    // ==============================
    // VIEW ALL BOOKINGS
    // ==============================
    @GetMapping
    public String getAllBookings(HttpSession session, Model model) {

        User user = (User) session.getAttribute("currentUser");

        if (user == null || user.getRole() != Role.STAFF) {
            return "redirect:/auth/login";
        }

        model.addAttribute("bookings", staffService.getAllBookings());

        return "staff/staff";
    }

    // ==============================
    // BOOKING DETAIL
    // ==============================
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Booking booking = staffService.getById(id);

        model.addAttribute("booking", booking);

        if (booking.getStatus() == BookingStatus.BOOKING) {

            model.addAttribute(
                    "availableRooms",
                    staffService.getAvailableRoomsByRoomType(
                            booking.getRoomType().getRoomTypeId())
            );
        }

        return "staff/staff";
    }

    // ==============================
    // CONFIRM + ASSIGN ROOM
    // ==============================
    @PostMapping("/{id}/assign-room")
    public String confirmBooking(
            @PathVariable Long id,
            @RequestParam Long roomId
    ) {

        staffService.assignRoom(id, roomId);

        return "redirect:/staff/" + id;
    }

    // ==============================
    // CHECK-IN
    // ==============================
    @PostMapping("/{id}/check-in")
    public String checkIn(@PathVariable Long id) {

        staffService.checkIn(id);

        return "redirect:/staff/" + id;
    }

    // ==============================
    // PAY
    // ==============================
    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id) {

        staffService.pay(id);

        return "redirect:/staff/" + id;
    }

    // ==============================
    // CHECK-OUT
    // ==============================
    @PostMapping("/{id}/check-out")
    public String checkOut(@PathVariable Long id) {

        staffService.checkOut(id);

        return "redirect:/staff/" + id;
    }

    // ==============================
    // TODAY CHECK-IN
    // ==============================
    @GetMapping("/checkin-today")
    public String todayCheckins(Model model) {

        model.addAttribute("bookings", staffService.getTodayCheckins());

        return "staff/checkin-today";
    }

    // ==============================
    // TODAY CHECK-OUT
    // ==============================
    @GetMapping("/checkout-today")
    public String todayCheckouts(Model model) {

        model.addAttribute("bookings", staffService.getTodayCheckouts());

        return "staff/checkout-today";
    }
}

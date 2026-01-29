package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.service.BookingService;
import com.hsf302.hotelmanagementproject.service.RoomTypeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;
    private final RoomTypeService roomTypeService;

// ==============================
// CHECKOUT PAGE (BOOKING + PAYMENT)
// ==============================
@GetMapping("/checkout")
public String checkout(
        @RequestParam Long roomTypeId,
        @RequestParam String checkinDate,
        @RequestParam String checkoutDate,
        HttpSession session,
        Model model
) {
    User user = (User) session.getAttribute("currentUser");
    if (user == null) {
        return "redirect:/login";
    }

    model.addAttribute("roomType",
            roomTypeService.getRoomTypeById(roomTypeId));
    model.addAttribute("checkinDate", checkinDate);
    model.addAttribute("checkoutDate", checkoutDate);

    return "booking/checkout";
}
    // ==============================
    // CONFIRM BOOKING
    // ==============================
    @PostMapping("/confirm")
    public String confirmBooking(
            @RequestParam Long roomTypeId,
            @RequestParam String checkinDate,
            @RequestParam String checkoutDate,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        LocalDateTime checkIn = LocalDateTime.parse(checkinDate);
        LocalDateTime checkOut = LocalDateTime.parse(checkoutDate);

        bookingService.createBooking(
                user.getUserId(),
                roomTypeId,
                checkIn,
                checkOut
        );

        return "redirect:/booking/success";
    }

    // ==============================
    // SUCCESS PAGE
    // ==============================
    @GetMapping("/success")
    public String success() {
        return "booking/success";
    }


    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "bookings",
                bookingService.getBookingsByUser(user.getUserId())
        );

        return "booking/booking_history";
    }

}





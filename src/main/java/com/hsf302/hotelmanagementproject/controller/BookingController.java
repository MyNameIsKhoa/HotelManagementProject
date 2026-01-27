package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.service.BookingService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/create")
    public String createBooking(@RequestParam Long roomTypeId,
                                @RequestParam String checkinDate,
                                @RequestParam String checkoutDate,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {


        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        Long userId = currentUser.getUserId();
        LocalDate checkIn = LocalDate.parse(checkinDate.substring(0, 10));
        LocalDate checkOut = LocalDate.parse(checkoutDate.substring(0, 10));
        try {
            bookingService.createBooking(userId, roomTypeId, checkIn, checkOut);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đặt phòng thành công! Chúng tôi đã ghi nhận booking của bạn."
            );

            return "redirect:/";

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );

            return "redirect:/booking/form?roomTypeId="
                    + roomTypeId
                    + "&checkinDate=" + checkinDate
                    + "&checkoutDate=" + checkoutDate;
        }}

    @GetMapping("/form")
    public String showBookingForm(@RequestParam Long roomTypeId,
                                  @RequestParam String checkinDate,
                                  @RequestParam String checkoutDate,
                                  Model model) {

        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);

        return "booking/booking_form";
    }

}

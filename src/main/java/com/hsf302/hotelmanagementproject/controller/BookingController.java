package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.DTO.CreateBookingRequest;
import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import com.hsf302.hotelmanagementproject.service.BookingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.util.Map;

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

            CreateBookingRequest req = new CreateBookingRequest();
            req.setRoomTypeId(roomTypeId);
            req.setCheckinDate(checkIn);
            req.setCheckoutDate(checkOut);

            Map<String, Object> result =
                    bookingService.createBooking(userId, req);

            String paymentUrl = (String) result.get("paymentUrl");

            // Redirect thẳng sang PayOS
            return "redirect:" + paymentUrl;

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );

            return "redirect:/booking/form?roomTypeId="
                    + roomTypeId
                    + "&checkinDate=" + checkinDate
                    + "&checkoutDate=" + checkoutDate;
        }
    }

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
    @PostMapping("/cancel")
    public String cancelBooking(
            @RequestParam Long bookingId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/auth/login";
        }

        Booking booking = bookingService.getById(bookingId);

        // Check quyền
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Bạn không có quyền hủy đặt phòng này."
            );
            return "redirect:/booking/my-bookings";
        }

        // Check trạng thái hợp lệ
        if (booking.getStatus() != BookingStatus.PENDING_DEPOSIT
                && booking.getStatus() != BookingStatus.CONFIRMED
                && booking.getStatus() != BookingStatus.ASSIGNED) {


            redirectAttributes.addFlashAttribute(
                    "error",
                    "Chỉ có thể hủy booking đang ở trạng thái BOOKING hoặc CONFIRMED."
            );
            return "redirect:/booking/my-bookings";
        }

        bookingService.cancelBooking(bookingId);

        redirectAttributes.addFlashAttribute(
                "success",
                "Hủy đặt phòng thành công."
        );

        return "redirect:/booking/my-bookings";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam Long roomTypeId,
                           @RequestParam String checkinDate,
                           @RequestParam String checkoutDate,
                           Model model) {

        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);

        // optional – nếu chưa có thì để null cũng không sao
        model.addAttribute("roomTypeName", "Standard Room");
        model.addAttribute("totalPrice", null);

        return "booking/checkout";
    }

}

package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.enums.PaymentMethod;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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
    public String dashboard(
            @RequestParam(required = false) String date,
            @RequestParam(required = false, defaultValue = "false") boolean todayCheckin,
            Model model
    ) {

        if (todayCheckin) {
            model.addAttribute(
                    "bookings",
                    staffService.getCheckinToday()
            );
        } else if (date != null && !date.isBlank()) {

            model.addAttribute(
                    "bookings",
                    staffService.searchByCheckinDate(LocalDate.parse(date))
            );

        } else {

            model.addAttribute(
                    "bookings",
                    staffService.getAllBookings()
            );
        }

        model.addAttribute("todayCheckin", todayCheckin);
        model.addAttribute("date", date);
        model.addAttribute("now", LocalDateTime.now());

        return "staff/dashboard";
    }

    // Gán phòng
    @PostMapping("/assign-room")
    public String assignRoom(
            @RequestParam Long bookingId,
            @RequestParam Long roomId
    ) {

        staffService.assignRoom(bookingId, roomId);

        return "redirect:/staff";
    }

    // Checkin
    @PostMapping("/checkin")
    public String checkin(@RequestParam Long bookingId) {

        staffService.checkIn(bookingId);

        return "redirect:/staff";
    }

    @PostMapping("/checkout/process")
    @ResponseBody
    public ResponseEntity<?> processCheckout(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod
    ) {
        try {
            // Chuyển String sang Enum
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod);

            // Gọi Service (Hàm checkoutWithPayment bạn đã cung cấp)
            // Lưu ý: staffService cần gọi sang bookingService hoặc tự implement hàm này
            Map<String, Object> result = staffService.checkoutWithPayment(bookingId, method);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/checkout-success")
    public String successPage() {
        return "staff/payment_success";
    }
    @GetMapping("/checkout-failed")
    public String checkoutFailed() {
        return "staff/payment_failed";
    }
}

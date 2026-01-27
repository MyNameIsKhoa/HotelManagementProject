package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.service.BookingService;
import org.springframework.ui.Model;

import com.hsf302.hotelmanagementproject.entity.Payment;
import com.hsf302.hotelmanagementproject.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public String showPayment(@PathVariable Long bookingId,
                              Model model) {

        Booking booking = bookingService.getById(bookingId);

        model.addAttribute("booking", booking);

        return "payment/payment";
    }

    @PostMapping("/{bookingId}/pay")
    public String processPayment(@PathVariable Long bookingId,
                                 Model model) {

        Payment payment = paymentService.createCashPayment(bookingId);

        model.addAttribute("payment", payment);
        model.addAttribute("bookingId", bookingId);

        return "payment/payment_success";

    }
}

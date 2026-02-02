package com.hsf302.hotelmanagementproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping("/success")
    public String successPage() {
        return "payment/success";
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "payment/cancel";
    }
}

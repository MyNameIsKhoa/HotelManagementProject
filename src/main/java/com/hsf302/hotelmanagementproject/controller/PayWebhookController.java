package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ResponseBody
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PayWebhookController {
    private final BookingService bookingService;

    /**
     * PayOS gọi vào đây khi thanh toán thành công
     */
//    @PostMapping(value = "/webhook", produces = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> payload) {
//
//        log.info("PayOS webhook received: {}", payload);
//
//        try {
//
//            Map<String, Object> data = (Map<String, Object>) payload.get("data");
//
//            Long orderCode = Long.valueOf(data.get("orderCode").toString());
//
//            bookingService.confirmDepositByOrderCode(orderCode);
//
//            return ResponseEntity.ok(Map.of(
//                    "message", "Deposit confirmed"
//            ));
//
//        } catch (Exception e) {
//
//            log.error("Webhook PayOS lỗi", e);
//
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", e.getMessage()
//            ));
//        }
//    }
    @PostMapping(value = "/webhook", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> payload) {

        log.info("PayOS webhook received: {}", payload);

        try {

            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            Long orderCode = Long.valueOf(data.get("orderCode").toString());

            bookingService.handlePayOSWebhook(orderCode);

            return ResponseEntity.ok(Map.of(
                    "message", "Webhook processed"
            ));

        } catch (Exception e) {

            log.error("Webhook PayOS lỗi", e);

            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
}
}

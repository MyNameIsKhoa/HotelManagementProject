package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Payment;

public interface PaymentService {
    Payment createCashPayment(Long bookingId);
}

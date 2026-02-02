package com.hsf302.hotelmanagementproject.service.impl;


import com.hsf302.hotelmanagementproject.repository.BookingRepository;
import com.hsf302.hotelmanagementproject.repository.PaymentRepository;
import com.hsf302.hotelmanagementproject.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;


}

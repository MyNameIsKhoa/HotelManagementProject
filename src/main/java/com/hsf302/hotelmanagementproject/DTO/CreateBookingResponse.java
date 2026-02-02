package com.hsf302.hotelmanagementproject.DTO;

import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreateBookingResponse {

    private Long bookingId;

    private BookingStatus status;

    private BigDecimal depositAmount;

    private String qrBase64;
}


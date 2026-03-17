package com.hsf302.hotelmanagementproject.DTO;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateBookingRequest {

    private Long userId;
    private Long roomTypeId;

    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
}

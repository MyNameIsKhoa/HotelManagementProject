package com.hsf302.hotelmanagementproject.DTO;


import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {

    private Long userId;
    private Long roomTypeId;

    private LocalDate checkinDate;
    private LocalDate checkoutDate;
}

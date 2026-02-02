package com.hsf302.hotelmanagementproject.entity.enums;

public enum BookingStatus {

    PENDING_DEPOSIT,   // mới tạo, chờ cọc
    CONFIRMED,         // đã cọc tiền
    ASSIGNED,          // staff gán phòng
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED
}

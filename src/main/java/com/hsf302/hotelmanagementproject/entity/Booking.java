package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room; // NULL khi đặt, gán khi check-in

    @Column(name = "checkin_date")
    private LocalDateTime checkinDate;

    @Column(name = "checkout_date")
    private LocalDateTime checkoutDate;

    @Column(name = "actual_checkin_time")
    private LocalDateTime actualCheckinTime;

    @Column(name = "actual_checkout_time")
    private LocalDateTime actualCheckoutTime;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    private String status;
    // PENDING / CONFIRMED / CANCELLED / CHECKED_IN / COMPLETED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}


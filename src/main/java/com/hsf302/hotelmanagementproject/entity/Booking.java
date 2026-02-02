package com.hsf302.hotelmanagementproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private LocalDateTime actualCheckinTime;
    private LocalDateTime actualCheckoutTime;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Payment> payments;

    private BigDecimal depositAmount;

    private Boolean depositPaid = false;

    private LocalDateTime depositPaidAt;
    private Long payosOrderCode;



}


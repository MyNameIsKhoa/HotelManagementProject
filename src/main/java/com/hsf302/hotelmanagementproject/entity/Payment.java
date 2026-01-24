package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private BigDecimal amount;

    private String method; // CASH / VNPAY

    private String status; // SUCCESS / FAILED / REFUNDED

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}


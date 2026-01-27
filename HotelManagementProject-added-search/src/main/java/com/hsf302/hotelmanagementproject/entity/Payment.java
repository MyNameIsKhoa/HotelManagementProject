package com.hsf302.hotelmanagementproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hsf302.hotelmanagementproject.entity.enums.PaymentMethod;
import com.hsf302.hotelmanagementproject.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method; // CASH / VNPAY

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // SUCCESS / FAILED / REFUNDED

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    @PrePersist
    protected void onCreate() {
        this.paidAt = LocalDateTime.now();
        this.transactionRef = UUID.randomUUID().toString();
    }
}


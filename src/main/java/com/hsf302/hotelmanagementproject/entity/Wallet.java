package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    // 1 user chỉ có 1 ví
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Số dư ví
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    // Ngày tạo ví
    private LocalDateTime createdAt = LocalDateTime.now();
}

package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "price_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "price_multiplier")
    private BigDecimal priceMultiplier;

    private String note;
}

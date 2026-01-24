package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_usages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_usage_id")
    private Long serviceUsageId;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String serviceName;
    private BigDecimal price;
    private Integer quantity;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}

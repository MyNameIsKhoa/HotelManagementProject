package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private Integer rating;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}


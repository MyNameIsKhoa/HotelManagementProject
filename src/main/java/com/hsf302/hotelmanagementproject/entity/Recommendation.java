package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

    @ManyToOne
    @JoinColumn(name = "ai_profile_id")
    private AiProfile aiProfile;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    private BigDecimal score;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();
}

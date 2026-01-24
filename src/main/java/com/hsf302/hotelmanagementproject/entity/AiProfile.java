package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_profile_id")
    private Long aiProfileId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "preference_vector", columnDefinition = "NVARCHAR(MAX)")
    private String preferenceVector;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}


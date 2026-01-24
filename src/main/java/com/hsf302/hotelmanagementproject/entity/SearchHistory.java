package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long searchId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String keyword;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String filters;

    @Column(name = "search_time")
    private LocalDateTime searchTime = LocalDateTime.now();
}


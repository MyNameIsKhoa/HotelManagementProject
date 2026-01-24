package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_thumbnail")
    private Boolean isThumbnail;
}


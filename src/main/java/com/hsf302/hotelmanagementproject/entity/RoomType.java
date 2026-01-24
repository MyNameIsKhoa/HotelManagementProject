package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "room_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Long roomTypeId;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    private Integer capacity;

    @Column(name = "total_rooms")
    private Integer totalRooms;
}

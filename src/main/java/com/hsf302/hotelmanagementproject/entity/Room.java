package com.hsf302.hotelmanagementproject.entity;

import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;

import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "room_number")
    private String roomNumber;
    @Enumerated(EnumType.STRING)
    private RoomStatus status; // AVAILABLE / DIRTY / MAINTENANCE
}


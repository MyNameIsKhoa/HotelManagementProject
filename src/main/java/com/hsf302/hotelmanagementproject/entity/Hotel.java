package com.hsf302.hotelmanagementproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long hotelId;

    private String name;
    private String address;
    private String phone;
}

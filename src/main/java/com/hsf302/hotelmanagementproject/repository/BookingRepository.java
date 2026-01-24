package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserUserId(Long userId);
}


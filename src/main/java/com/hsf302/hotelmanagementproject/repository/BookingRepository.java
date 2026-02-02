package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserUserId(Long userId);
    Optional<Booking> findByPayosOrderCode(Long payosOrderCode);

    @Query("""
SELECT COUNT(b)
FROM Booking b
WHERE b.roomType.id = :roomTypeId
  AND b.status IN ('CONFIRMED','CHECKED_IN')
  AND b.checkinDate < :checkout
  AND b.checkoutDate > :checkin
""")
    long countActiveBookings(Long roomTypeId,
                             LocalDateTime checkin,
                             LocalDateTime checkout);


    List<Booking> findByStatus(BookingStatus status);

    // All bookings newest first
    List<Booking> findAllByOrderByCreatedAtDesc();

    // Search theo ngày check-in
    List<Booking> findByCheckinDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    // Booking chuẩn bị check-in hôm nay
    @Query("""
        SELECT b FROM Booking b
        WHERE b.status IN ('CONFIRMED','ASSIGNED')
          AND b.checkinDate >= :start
          AND b.checkinDate < :end
    """)
    List<Booking> findCheckinToday(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Checkout hôm nay
    @Query("""
        SELECT b FROM Booking b
        WHERE b.status = 'CHECKED_IN'
          AND DATE(b.checkoutDate) = :date
    """)
    List<Booking> findCheckoutToday(
            @Param("date") LocalDate date
    );

}


package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserUserId(Long userId);

    @Query("""
SELECT COUNT(b)
FROM Booking b
WHERE b.roomType.roomTypeId = :roomTypeId
  AND b.status IN ('BOOKING','CONFIRMED','CHECKED_IN')
  AND b.checkinDate < :checkout
  AND b.checkoutDate > :checkin
""")
    long countActiveBookings(@Param("roomTypeId") Long roomTypeId,
                             @Param("checkin") LocalDateTime checkin,
                             @Param("checkout") LocalDateTime checkout);


    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByCheckinDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
       SELECT b FROM Booking b
       WHERE b.status = :status
         AND DATE(b.checkoutDate) = :date
    """)
    List<Booking> findCheckoutToday(
            @Param("status") BookingStatus status,
            @Param("date") LocalDate date
    );


}


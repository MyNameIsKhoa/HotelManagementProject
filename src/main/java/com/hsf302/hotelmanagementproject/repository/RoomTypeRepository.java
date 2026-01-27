package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("""
        SELECT rt, COUNT(r.roomId) - COUNT(b.bookingId)
        FROM RoomType rt
        JOIN rt.hotel h
        JOIN Room r ON r.roomType = rt
        LEFT JOIN Booking b
            ON b.room = r
            AND b.status IN ('CONFIRMED', 'CHECKED_IN')
            AND NOT (b.checkoutDate <= :checkin OR b.checkinDate >= :checkout)
        WHERE h.hotelId = :hotelId
        GROUP BY rt
        HAVING COUNT(r.roomId) - COUNT(b.bookingId) > 0
    """)
    List<Object[]> findAvailableRoomTypesByHotel(
            @Param("hotelId") Long hotelId,
            @Param("checkin") LocalDateTime checkin,
            @Param("checkout") LocalDateTime checkout
    );
    @Query("""
    SELECT COUNT(r.roomId) - COUNT(b.bookingId)
    FROM Room r
    LEFT JOIN Booking b
        ON b.room = r
        AND b.status IN ('CONFIRMED', 'CHECKED_IN')
        AND NOT (b.checkoutDate <= :checkin OR b.checkinDate >= :checkout)
    WHERE r.roomType.roomTypeId = :roomTypeId
""")
    int countAvailableRooms(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkin") LocalDateTime checkin,
            @Param("checkout") LocalDateTime checkout
    );

}
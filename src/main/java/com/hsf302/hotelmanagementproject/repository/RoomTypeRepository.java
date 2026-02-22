package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("""
SELECT rt,
       (rt.totalRooms - COUNT(b))
FROM RoomType rt
LEFT JOIN Booking b 
    ON b.roomType.roomTypeId = rt.roomTypeId
    AND (
        b.checkinDate < :checkout
        AND b.checkoutDate > :checkin
    )
WHERE rt.hotel.hotelId = :hotelId
GROUP BY rt
""")
    List<Object[]> findAvailableRoomTypesByHotel(
            @Param("hotelId") Long hotelId,
            @Param("checkin") LocalDateTime checkin,
            @Param("checkout") LocalDateTime checkout
    );
    @Query("""
SELECT DISTINCT rt
FROM RoomType rt
LEFT JOIN FETCH rt.images
WHERE rt IN :roomTypes
""")
    List<RoomType> fetchImages(@Param("roomTypes") List<RoomType> roomTypes);



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

    @Query("""
SELECT DISTINCT rt
FROM RoomType rt
LEFT JOIN FETCH rt.images
""")
    List<RoomType> findTop3ByOrderByRoomTypeIdAsc(Pageable pageable);

    default List<RoomType> findTop3ByOrderByRoomTypeIdAsc() {
        return findTop3ByOrderByRoomTypeIdAsc(PageRequest.of(0,3));
    }

}
package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
    SELECT r FROM Room r
    WHERE r.roomType.roomTypeId = :typeId
      AND r.status = com.hsf302.hotelmanagementproject.entity.enums.RoomStatus.AVAILABLE
""")
    List<Room> findAvailableByRoomType(@Param("typeId") Long typeId);

    long countByRoomType_RoomTypeId(Long roomTypeId);
    List<Room> findByRoomType_RoomTypeIdAndStatus(Long roomTypeId, RoomStatus status);
    @Query("""
SELECT r.roomId
FROM Room r
JOIN Booking b ON b.room.roomId = r.roomId
WHERE b.status IN ('CONFIRMED','CHECKED_IN')
  AND b.checkinDate < :checkout
  AND b.checkoutDate > :checkin
""")
    List<Long> findOccupiedRoomIds(
            @Param("checkin") LocalDateTime checkin,
            @Param("checkout") LocalDateTime checkout
    );
    List<Room> findByRoomType_RoomTypeId(Long roomTypeId);

}

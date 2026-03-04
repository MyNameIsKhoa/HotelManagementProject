package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Tính average rating theo room type
     * Trả về Object[] = { roomTypeId (Long), avgRating (Double), reviewCount (Long) }
     */
    @Query("""
        SELECT b.roomType.roomTypeId, AVG(CAST(r.rating AS double)), COUNT(r)
        FROM Review r
        JOIN r.booking b
        GROUP BY b.roomType.roomTypeId
    """)
    List<Object[]> findAverageRatingByRoomType();

    /**
     * Lấy các review của 1 room type cụ thể
     */
    @Query("""
        SELECT r FROM Review r
        JOIN r.booking b
        WHERE b.roomType.roomTypeId = :roomTypeId
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByRoomTypeId(@Param("roomTypeId") Long roomTypeId);
}


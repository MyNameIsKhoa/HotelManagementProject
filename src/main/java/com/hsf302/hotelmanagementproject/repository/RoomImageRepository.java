package com.hsf302.hotelmanagementproject.repository;


import com.hsf302.hotelmanagementproject.entity.RoomImage;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {

    List<RoomImage> findByRoomTypeAndIsThumbnailFalse(RoomType roomType);

    List<RoomImage> findByRoomType(RoomType roomType);

        RoomImage findFirstByRoomTypeAndIsThumbnailTrue(RoomType roomType);
    }



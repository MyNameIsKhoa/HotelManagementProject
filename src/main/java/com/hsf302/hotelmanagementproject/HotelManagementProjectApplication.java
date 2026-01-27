package com.hsf302.hotelmanagementproject;

import com.hsf302.hotelmanagementproject.entity.RoomImage;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.repository.RoomImageRepository;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class HotelManagementProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelManagementProjectApplication.class, args);
    }
    @Bean
    CommandLineRunner seedRoomImages(
            RoomTypeRepository roomTypeRepository,
            RoomImageRepository roomImageRepository
    ) {
        return args -> {

            // Tránh insert trùng
            if (roomImageRepository.count() > 0) {
                return;
            }

            insertImages(roomTypeRepository, roomImageRepository,
                    1L,
                    List.of(
                            "https://images.unsplash.com/photo-1501117716987-c8e1ecb210d1",
                            "https://images.unsplash.com/photo-1566073771259-6a8506099945",
                            "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb",
                            "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af"
                    )
            );

            insertImages(roomTypeRepository, roomImageRepository,
                    2L,
                    List.of(
                            "https://images.unsplash.com/photo-1582719478181-2f3c7b9a3d52",
                            "https://images.unsplash.com/photo-1576675784201-0e142b423952",
                            "https://images.unsplash.com/photo-1618773928121-c32242e63f39",
                            "https://images.unsplash.com/photo-1560067174-894f8e8e03f2"
                    )
            );

            insertImages(roomTypeRepository, roomImageRepository,
                    3L,
                    List.of(
                            "https://images.unsplash.com/photo-1590490360182-c33d57733427",
                            "https://images.unsplash.com/photo-1560448070-c6d41b521a7c",
                            "https://images.unsplash.com/photo-1554995207-c18c203602cb",
                            "https://images.unsplash.com/photo-1560185127-6a8c1b3b7b2b"
                    )
            );

            System.out.println("✅ Seed room images completed");
        };
    }

    private void insertImages(
            RoomTypeRepository roomTypeRepository,
            RoomImageRepository roomImageRepository,
            Long roomTypeId,
            List<String> urls
    ) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("RoomType not found: " + roomTypeId));

        for (String url : urls) {
            RoomImage image = new RoomImage();
            image.setRoomType(roomType);
            image.setImageUrl(url);
            roomImageRepository.save(image);
        }
    }

}

package com.hsf302.hotelmanagementproject.config;

import com.hsf302.hotelmanagementproject.entity.Hotel;
import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import com.hsf302.hotelmanagementproject.repository.HotelRepository;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) throws Exception {

        if (hotelRepository.count() == 0) {

            // 1. Tạo Khách sạn
            Hotel h1 = hotelRepository.save(
                    Hotel.builder()
                            .name("FPT Hotel Đà Nẵng")
                            .address("123 Nguyễn Văn Linh, Đà Nẵng")
                            .phone("0901234567") // Check your actual field name for phone
                            .build());

            Hotel h2 = hotelRepository.save(
                    Hotel.builder()
                            .name("Saigon Luxury Hotel")
                            .address("45 Lê Lợi, Quận 1, TP.HCM")
                            .phone("0907654321")
                            .build());

            Hotel h3 = hotelRepository.save(
                    Hotel.builder()
                            .name("Hanoi Central Hotel")
                            .address("89 Trần Hưng Đạo, Hà Nội")
                            .phone("0912345678")
                            .build());

            // ================= ROOM TYPES =================

            RoomType standard = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h1)
                            .name("Standard Room")
                            .description("Phòng tiêu chuẩn, phù hợp 2 người")
                            .basePrice(BigDecimal.valueOf(800000))
                            .capacity(2)
                            .totalRooms(10)
                            .build());

            RoomType deluxe = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h1)
                            .name("Deluxe Room")
                            .description("Phòng cao cấp có view đẹp")
                            .basePrice(BigDecimal.valueOf(1200000))
                            .capacity(3)
                            .totalRooms(8)
                            .build());

            RoomType suite = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h1)
                            .name("Suite Room")
                            .description("Phòng hạng sang, phòng khách riêng")
                            .basePrice(BigDecimal.valueOf(2000000))
                            .capacity(4)
                            .totalRooms(5)
                            .build());

            // ================= ROOMS =================

            roomRepository.saveAll(List.of(
                    new Room(null, deluxe, "201", RoomStatus.AVAILABLE),
                    new Room(null, deluxe, "202", RoomStatus.AVAILABLE),
                    new Room(null, deluxe, "203", RoomStatus.AVAILABLE),

                    new Room(null, suite, "301", RoomStatus.AVAILABLE),
                    new Room(null, suite, "302", RoomStatus.AVAILABLE)
            ));
        }
    }
}

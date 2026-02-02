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
    public void run(String... args) {

        if (hotelRepository.count() == 0) {

            // ================= HOTELS =================
            Hotel h1 = hotelRepository.save(
                    Hotel.builder()
                            .name("FPT Hotel Đà Nẵng")
                            .address("123 Nguyễn Văn Linh, Đà Nẵng")
                            .phone("0901234567")
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

            // H1
            RoomType standardH1 = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h1)
                            .name("Standard Room")
                            .description("Phòng tiêu chuẩn, phù hợp 2 người")
                            .basePrice(BigDecimal.valueOf(800000))
                            .capacity(2)
                            .totalRooms(10)
                            .build());

            RoomType deluxeH1 = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h1)
                            .name("Deluxe Room")
                            .description("Phòng cao cấp có view đẹp")
                            .basePrice(BigDecimal.valueOf(5000))
                            .capacity(3)
                            .totalRooms(8)
                            .build());

            RoomType suiteH1 = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h1)
                            .name("Suite Room")
                            .description("Phòng hạng sang, phòng khách riêng")
                            .basePrice(BigDecimal.valueOf(5000))
                            .capacity(4)
                            .totalRooms(5)
                            .build());

            // H2
            RoomType deluxeH2 = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h2)
                            .name("Deluxe Room")
                            .description("Phòng cao cấp trung tâm SG")
                            .basePrice(BigDecimal.valueOf(1500000))
                            .capacity(3)
                            .totalRooms(6)
                            .build());

            // H3
            RoomType standardH3 = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h3)
                            .name("Standard Room")
                            .description("Phòng tiêu chuẩn Hà Nội")
                            .basePrice(BigDecimal.valueOf(700000))
                            .capacity(2)
                            .totalRooms(10)
                            .build());

            RoomType suiteH3 = roomTypeRepository.save(
                    RoomType.builder()
                            .hotel(h3)
                            .name("Suite Room")
                            .description("Suite cao cấp Hà Nội")
                            .basePrice(BigDecimal.valueOf(2200000))
                            .capacity(4)
                            .totalRooms(4)
                            .build());

            // ================= ROOMS =================

            roomRepository.saveAll(List.of(

                    // ---- H1 ----
                    new Room(null, standardH1, "101", RoomStatus.AVAILABLE),
                    new Room(null, standardH1, "102", RoomStatus.AVAILABLE),

                    new Room(null, deluxeH1, "201", RoomStatus.AVAILABLE),
                    new Room(null, deluxeH1, "202", RoomStatus.AVAILABLE),

                    new Room(null, suiteH1, "301", RoomStatus.AVAILABLE),
                    new Room(null, suiteH1, "302", RoomStatus.AVAILABLE),

                    // ---- H2 ----
                    new Room(null, deluxeH2, "201", RoomStatus.AVAILABLE),
                    new Room(null, deluxeH2, "202", RoomStatus.AVAILABLE),

                    // ---- H3 ----
                    new Room(null, standardH3, "101", RoomStatus.AVAILABLE),
                    new Room(null, standardH3, "102", RoomStatus.AVAILABLE),

                    new Room(null, suiteH3, "301", RoomStatus.AVAILABLE),
                    new Room(null, suiteH3, "302", RoomStatus.AVAILABLE)

            ));
        }
    }
}

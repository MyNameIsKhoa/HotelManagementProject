package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.RoomType;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import com.hsf302.hotelmanagementproject.repository.BookingRepository;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import com.hsf302.hotelmanagementproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService  {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    @Override
    public Booking createBooking(Long userId, Long roomTypeId, LocalDate checkinDate, LocalDate checkoutDate) {

        if (!checkoutDate.isAfter(checkinDate)) {
            throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng."));

        long activeBookings = bookingRepository.countActiveBookings(
                roomTypeId,
                checkinDate.atStartOfDay(),
                checkoutDate.atStartOfDay()
        );

        long totalRooms = roomRepository.countByRoomType_RoomTypeId(roomTypeId);

        if (activeBookings >= totalRooms) {
            throw new RuntimeException("Loại phòng này đã hết trong thời gian bạn chọn");
        }


        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoomType(roomType);
        booking.setRoom(null); // staff gán khi check-in
        booking.setCheckinDate(checkinDate.atStartOfDay());
        booking.setCheckoutDate(checkoutDate.atStartOfDay());
        booking.setStatus(BookingStatus.BOOKING);

        long nights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

        booking.setTotalPrice(
                roomType.getBasePrice()
                        .multiply(BigDecimal.valueOf(nights))
        );

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng"));
    }

}

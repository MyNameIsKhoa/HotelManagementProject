package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.Payment;
import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import com.hsf302.hotelmanagementproject.entity.enums.PaymentMethod;
import com.hsf302.hotelmanagementproject.entity.enums.PaymentStatus;
import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import com.hsf302.hotelmanagementproject.repository.BookingRepository;
import com.hsf302.hotelmanagementproject.repository.PaymentRepository;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Override
    public List<Booking> getTodayCheckins() {
        LocalDate today = LocalDate.now();
        return bookingRepository.findByCheckinDateBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }

    @Override
    public List<Booking> getTodayCheckouts() {
        return bookingRepository.findCheckoutToday(
                BookingStatus.CHECKED_IN,
                LocalDate.now()
        );
    }
   @Transactional
    // STAFF gán phòng cho booking
    @Override
    public void assignRoom(Long bookingId, Long roomId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow();

        if (booking.getStatus() != BookingStatus.BOOKING) {
            throw new IllegalStateException("Booking chưa ở trạng thái BOOKING");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow();

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Room không sẵn sàng");
        }

        if (!room.getRoomType().getRoomTypeId()
                .equals(booking.getRoomType().getRoomTypeId())) {

            throw new IllegalStateException("Room không đúng loại đã đặt");
        }

        booking.setRoom(room);
        booking.setStatus(BookingStatus.CONFIRMED);

        room.setStatus(RoomStatus.BOOKED);

        roomRepository.save(room);
        bookingRepository.save(booking);
    }


    // STAFF check-in khách
    @Override
    public void checkIn(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow();

        if (booking.getRoom() == null) {
            throw new IllegalStateException("Chưa gán phòng");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking chưa được confirm");
        }

        booking.setActualCheckinTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_IN);

        bookingRepository.save(booking);
    }


    // STAFF thu tiền
    @Override
    public void pay(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.CHECKED_IN)) {
            throw new IllegalStateException("Chỉ được thanh toán khi khách đã check-in");
        }

        if (booking.getPayment() != null) {
            throw new IllegalStateException("Booking đã thanh toán");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaidAt(LocalDateTime.now());
        payment.setMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.SUCCESS);

        booking.setPayment(payment);
        booking.setStatus(BookingStatus.PAID);

        paymentRepository.save(payment);
    }

    // STAFF checkout khách
    @Override
    public void checkOut(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Room room = booking.getRoom();

        if (room == null) {
            throw new IllegalStateException("Booking chưa được gán phòng");
        }

        booking.setActualCheckoutTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.COMPLETED);

        room.setStatus(RoomStatus.DIRTY);

        bookingRepository.save(booking);
    }
    @Override
    public List<Room> getAvailableRoomsByRoomType(Long roomTypeId) {
        // Gọi Repository để tìm phòng theo Type ID và Status = AVAILABLE
        // Đảm bảo bạn đã sửa RoomRepository như hướng dẫn trước (dùng findByRoomType_RoomTypeIdAndStatus)
        // Đúng vì truyền Enum RoomStatus.AVAILABLE
        return roomRepository.findByRoomType_RoomTypeIdAndStatus(roomTypeId, RoomStatus.AVAILABLE);
    }

}


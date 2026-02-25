package com.hsf302.hotelmanagementproject.service.impl;

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
import com.hsf302.hotelmanagementproject.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final Random random = new Random();
    private final PayOS payOS;
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Booking> searchByCheckinDate(LocalDate date) {
        return bookingRepository.findByCheckinDateBetween(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );
    }

    @Override
    public List<Booking> getCheckinToday() {
        LocalDate today = LocalDate.now();

        return bookingRepository.findCheckinToday(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }

    @Override
    public List<Booking> getTodayCheckouts() {
        return bookingRepository.findCheckoutToday(LocalDate.now());
    }

    @Override
    public Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // ================= ACTION =================

    @Override
    public void assignRoom(Long bookingId, Long roomId) {

        Booking booking = getBooking(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking chưa sẵn sàng để gán phòng");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow();

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Phòng không available");
        }

        if (!room.getRoomType().getRoomTypeId()
                .equals(booking.getRoomType().getRoomTypeId())) {
            throw new IllegalStateException("Sai loại phòng");
        }

        booking.setRoom(room);
        booking.setStatus(BookingStatus.ASSIGNED);

        room.setStatus(RoomStatus.BOOKED);

        bookingRepository.save(booking);
        roomRepository.save(room);
    }

    @Override
    public void checkIn(Long bookingId) {

        Booking booking = getBooking(bookingId);

        if (booking.getStatus() != BookingStatus.ASSIGNED) {
            throw new IllegalStateException("Booking chưa được gán phòng");
        }

        // Kiểm tra ngày: không cho check-in trước ngày checkinDate
        if (LocalDate.now().isBefore(booking.getCheckinDate().toLocalDate())) {
            throw new IllegalStateException("Chưa đến ngày check-in. Ngày check-in: " + booking.getCheckinDate().toLocalDate());
        }

        booking.setActualCheckinTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_IN);

        bookingRepository.save(booking);
    }

    @Override
    public void checkOut(Long bookingId) {

        Booking booking = getBooking(bookingId);

        // Kiểm tra ngày: không cho check-out trước ngày checkoutDate
        if (LocalDate.now().isBefore(booking.getCheckoutDate().toLocalDate())) {
            throw new IllegalStateException("Chưa đến ngày check-out. Ngày check-out: " + booking.getCheckoutDate().toLocalDate());
        }

        Room room = booking.getRoom();

        booking.setActualCheckoutTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_OUT);

        room.setStatus(RoomStatus.DIRTY);

        bookingRepository.save(booking);
        roomRepository.save(room);
    }

    @Override
    public List<Room> getAvailableRoomsByRoomType(Long roomTypeId) {
        return roomRepository.findByRoomType_RoomTypeIdAndStatus(
                roomTypeId,
                RoomStatus.AVAILABLE
        );
    }

    @Override
    @Transactional
    public Map<String, Object> checkoutWithPayment(Long bookingId, PaymentMethod method) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Booking chưa check-in, không thể check-out.");
        }

        // Kiểm tra ngày: không cho check-out trước ngày checkoutDate
        if (LocalDate.now().isBefore(booking.getCheckoutDate().toLocalDate())) {
            throw new IllegalStateException("Chưa đến ngày check-out. Ngày check-out: " + booking.getCheckoutDate().toLocalDate());
        }

        Room room = booking.getRoom();

        // ===== TÍNH TIỀN =====
        BigDecimal totalAmount = booking.getTotalPrice();
        BigDecimal deposit = booking.getDepositAmount();
        BigDecimal remainAmount = totalAmount.subtract(deposit);

        // ===== NẾU ĐÃ THANH TOÁN HẾT =====
        if (remainAmount.compareTo(BigDecimal.ZERO) <= 0) {

            booking.setActualCheckoutTime(LocalDateTime.now());
            booking.setStatus(BookingStatus.CHECKED_OUT);

            if (room != null) {
                room.setStatus(RoomStatus.DIRTY);
            }

            bookingRepository.save(booking);
            roomRepository.save(room);

            return Map.of(
                    "message", "Booking đã được thanh toán đủ trước đó.",
                    "status", "SUCCESS",
                    "remainAmount", BigDecimal.ZERO
            );
        }

        // ===== TẠO PAYMENT =====
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(remainAmount);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        // ================= CASH =================
        if (method == PaymentMethod.CASH) {

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            booking.setActualCheckoutTime(LocalDateTime.now());
            booking.setStatus(BookingStatus.CHECKED_OUT);

            if (room != null) {
                room.setStatus(RoomStatus.DIRTY);
                roomRepository.save(room);
            }

            paymentRepository.save(payment);
            bookingRepository.save(booking);

            return Map.of(
                    "message", "Thanh toán tiền mặt và checkout thành công",
                    "status", "SUCCESS",
                    "remainAmount", remainAmount
            );
        }

        // ================= PAYOS =================
        if (method == PaymentMethod.VNPAY) {

            try {
                String randomSuffix = String.format("%03d", random.nextInt(1000));
                long orderCode = Long.parseLong("20" + booking.getBookingId() + randomSuffix);

                long amountToPay = remainAmount.longValue();

                String description = "Checkout booking #" + booking.getBookingId();

                CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                        .orderCode(orderCode)
                        .amount(amountToPay)
                        .description(description)
                        .returnUrl("http://localhost:8080/staff/checkout-success")
                        .cancelUrl("http://localhost:8080/staff/checkout-failed")
                        .build();

                CreatePaymentLinkResponse response =
                        payOS.paymentRequests().create(request);

                payment.setPayosOrderCode(String.valueOf(orderCode));

                paymentRepository.save(payment);

                return Map.of(
                        "message", "Tạo link thanh toán thành công",
                        "status", "PENDING",
                        "paymentUrl", response.getCheckoutUrl(),
                        "amount", amountToPay
                );

            } catch (Exception e) {
                throw new RuntimeException("Lỗi tạo link PayOS: " + e.getMessage());
            }
        }

        throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ");
    }


}


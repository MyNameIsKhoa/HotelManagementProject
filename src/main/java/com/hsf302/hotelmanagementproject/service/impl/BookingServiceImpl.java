package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.DTO.CreateBookingRequest;
import com.hsf302.hotelmanagementproject.entity.*;
import com.hsf302.hotelmanagementproject.entity.enums.BookingStatus;
import com.hsf302.hotelmanagementproject.entity.enums.PaymentStatus;
import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import com.hsf302.hotelmanagementproject.repository.*;
import com.hsf302.hotelmanagementproject.service.BookingService;
import com.hsf302.hotelmanagementproject.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Thêm log
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.payos.PayOS; // Import PayOS
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import java.util.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final PayOS payOS; // Inject PayOS (nhớ config Bean PayOS như dự án cũ)
    private final Random random = new Random();
    private final PaymentRepository paymentRepository;
    @Override
    @Transactional
    public Map<String, Object> createBooking(Long userId, CreateBookingRequest req) {

        // 1. Lấy thông tin User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại."));

        // (Giống EV: Check Active/Lock user nếu cần)
        // if (user.getStatus() != AccountStatus.ACTIVE) ...

        // 2. Validate Ngày tháng
        if (!req.getCheckoutDate().isAfter(req.getCheckinDate())) {
            throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");
        }

        LocalDateTime checkInTime = req.getCheckinDate().atStartOfDay();
        LocalDateTime checkOutTime = req.getCheckoutDate().atStartOfDay();

        // 3. Lấy thông tin RoomType (Thay vì lấy Vehicle)
        RoomType roomType = roomTypeRepository.findById(req.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng."));

        // 4. Validate số lượng phòng (Logic thay thế cho check trùng lịch xe)
        // Đếm số đơn đặt phòng đang active trong khoảng thời gian này
        long activeBookings = bookingRepository.countActiveBookings(
                roomType.getRoomTypeId(),
                checkInTime,
                checkOutTime
        );

        // Đếm tổng số phòng của loại này
        long totalRooms = roomRepository.countByRoomType_RoomTypeId(roomType.getRoomTypeId());

        // Nếu số đơn >= số phòng -> Hết phòng
        if (activeBookings >= totalRooms) {
            throw new RuntimeException("Loại phòng này đã hết trong thời gian bạn chọn.");
        }

        // 5. Tính tiền
        long nights = ChronoUnit.DAYS.between(req.getCheckinDate(), req.getCheckoutDate());
        BigDecimal totalPrice = roomType.getBasePrice().multiply(BigDecimal.valueOf(nights));

        // Tiền cọc (Ví dụ fix cứng 2000 VND để test PayOS, hoặc 50% giá trị)
        BigDecimal depositAmount = BigDecimal.valueOf(2000);

        // 6. Lưu Booking (PENDING_DEPOSIT)
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoomType(roomType);
        booking.setRoom(null); // Chưa gán phòng cụ thể (Assign sau)
        booking.setCheckinDate(checkInTime);
        booking.setCheckoutDate(checkOutTime);
        booking.setStatus(BookingStatus.PENDING_DEPOSIT); // Trạng thái chờ thanh toán
        booking.setTotalPrice(totalPrice);
        booking.setDepositAmount(depositAmount);
        booking.setDepositPaid(false);

        booking = bookingRepository.save(booking);

        // 7. Tích hợp PayOS (Giống hệt logic EV Rental)
        try {
            // Tạo mã đơn hàng: Prefix 10 + BookingID + Random 4 số
            // Ví dụ: BookingID 5 -> 1051234
            String randomSuffix = String.format("%04d", random.nextInt(10000));
            long orderCode = Long.parseLong("10" + booking.getBookingId() + randomSuffix);
            final long amount = 2000L;
            String description = "Coc phong " + booking.getBookingId();
            String returnUrl = "http://localhost:8080/payment/success";
            String cancelUrl = "http://localhost:8080/payment/cancel";


            // Tạo ItemData (PayOS yêu cầu có ít nhất 1 item nếu dùng PaymentData builder)
            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .amount(amount)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .build();

            // Gọi tạo link
            CreatePaymentLinkResponse paymentResult = payOS.paymentRequests().create(paymentData);
            booking.setPayosOrderCode(orderCode);
            bookingRepository.save(booking);

            return Map.of(
                    "message", "Yêu cầu đặt phòng thành công. Vui lòng thanh toán cọc .",
                    "bookingId", booking.getBookingId(),
                    "paymentUrl", paymentResult.getCheckoutUrl()
            );

        } catch (Exception e) {
            log.error("Lỗi khi tạo link thanh toán PayOS: {}", e.getMessage());
            // Có thể xóa booking vừa tạo nếu lỗi thanh toán để tránh rác DB
            // bookingRepository.delete(booking);
            throw new RuntimeException("Không thể tạo link thanh toán: " + e.getMessage());
        }
    }

    // Hàm này sẽ được gọi bởi Controller Webhook khi PayOS báo tiền về
    @Override
    @Transactional
    public Booking confirmDepositByOrderCode(Long orderCode) {

        Booking booking = bookingRepository
                .findByPayosOrderCode(orderCode)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy booking với orderCode PayOS: " + orderCode)
                );

        if (Boolean.TRUE.equals(booking.getDepositPaid())) {
            log.info("Booking {} đã thanh toán cọc trước đó.", booking.getBookingId());
            return booking;
        }

        booking.setDepositPaid(true);
        booking.setDepositPaidAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    // Hàm dành cho Staff: Gán phòng sau khi khách đã cọc
    @Override
    @Transactional
    public Booking assignRoom(Long bookingId, Long roomId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        // Chỉ cho phép gán phòng nếu khách đã cọc
        if (!booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            // Có thể mở rộng: Nếu là CONFIRMED (đã xác nhận nhưng chưa gán phòng) cũng ok
            throw new RuntimeException("Booking chưa thanh toán cọc hoặc trạng thái không hợp lệ.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng id: " + roomId));

        // Kiểm tra xem phòng này có đúng loại khách đặt không
        if (!room.getRoomType().getRoomTypeId().equals(booking.getRoomType().getRoomTypeId())) {
            throw new RuntimeException("Phòng được gán không thuộc loại phòng khách đã đặt.");
        }

        // Kiểm tra phòng có trống trong khoảng thời gian đó không (cẩn thận double booking)
        // ... (Logic kiểm tra phòng trống cụ thể) ...

        booking.setRoom(room);
        booking.setStatus(BookingStatus.ASSIGNED); // Hoặc ASSIGNED, tùy enum của bạn

        // Có thể gửi email thông báo số phòng cho khách tại đây

        return bookingRepository.save(booking);
    }

    @Transactional
    public void handlePayOSWebhook(Long orderCode) {
        String codeStr = String.valueOf(orderCode);
        if (codeStr.startsWith("10")) {
            confirmDepositByOrderCode(orderCode);
        } else if (codeStr.startsWith("20")) {
            confirmCheckoutByOrderCode(codeStr);
        }
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserUserId(userId);
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng"));
    }

    @Override
    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }
    @Autowired
    private WalletService walletService;
    @Transactional

    @Override
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));


        BookingStatus oldStatus = booking.getStatus();

        if (
                oldStatus != BookingStatus.CONFIRMED&& oldStatus!= BookingStatus.ASSIGNED) {
            throw new RuntimeException("Invalid booking status");
        }


        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);


        if (oldStatus == BookingStatus.CONFIRMED || oldStatus == BookingStatus.ASSIGNED) {
            walletService.refund(
                    booking.getUser(),
                    booking.getTotalPrice(),
                    booking.getBookingId()
            );
        }
    }



// Import thêm các Enum cần thiết: PaymentStatus, RoomStatus...

    private void confirmCheckoutByOrderCode(String orderCodeStr) {
        // 1. Tìm Payment theo OrderCode (Lưu ý: PaymentRepository phải có hàm findByPayosOrderCode)
        Payment payment = paymentRepository.findByPayosOrderCode(orderCodeStr)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Payment với mã: " + orderCodeStr));

        // Nếu đã xử lý rồi thì bỏ qua
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        // 2. Update Payment -> SUCCESS
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // 3. Update Booking -> CHECKED_OUT
        Booking booking = payment.getBooking();
        booking.setActualCheckoutTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_OUT);
        bookingRepository.save(booking);

        // 4. Update Room -> DIRTY
        if (booking.getRoom() != null) {
            booking.getRoom().setStatus(RoomStatus.DIRTY);
            roomRepository.save(booking.getRoom());
        }

        log.info("Webhook: Xác nhận CHECKOUT thành công cho Booking {}", booking.getBookingId());
    }



}

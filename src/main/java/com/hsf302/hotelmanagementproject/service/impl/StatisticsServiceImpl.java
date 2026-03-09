package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import com.hsf302.hotelmanagementproject.repository.BookingRepository;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.repository.UserRepository;
import com.hsf302.hotelmanagementproject.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private static final Map<String, String> ROOM_STATUS_VI = Map.of(
            "AVAILABLE", "Trống",
            "BOOKED", "Đã đặt",
            "MAINTENANCE", "Đang sửa chữa",
            "DIRTY", "Chờ dọn"
    );

    @Override
    public Map<String, Object> getRoomStatusStats() {
        List<Object[]> rows = roomRepository.countRoomsByStatus();
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : rows) {
            RoomStatus status = (RoomStatus) row[0];
            Long count = (Long) row[1];
            labels.add(ROOM_STATUS_VI.getOrDefault(status.name(), status.name()));
            data.add(count);
        }

        for (RoomStatus rs : RoomStatus.values()) {
            String viLabel = ROOM_STATUS_VI.getOrDefault(rs.name(), rs.name());
            if (!labels.contains(viLabel)) {
                labels.add(viLabel);
                data.add(0L);
            }
        }
        return Map.of("labels", labels, "data", data);
    }

    @Override
    public Map<String, Object> getRevenueByRoomTypeStats() {
        List<Object[]> rows = bookingRepository.sumRevenueByRoomType();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add((String) row[0]);
            data.add((BigDecimal) row[1]);
        }
        return Map.of("labels", labels, "data", data);
    }

    @Override
    public Map<String, Object> getDashboardSummary() {
        long totalRooms = roomRepository.findByIsDeletedFalse().size();
        long totalBookings = bookingRepository.count();
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        long totalUsers = userRepository.count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalRooms", totalRooms);
        summary.put("totalBookings", totalBookings);
        summary.put("totalRevenue", totalRevenue);
        summary.put("totalUsers", totalUsers);
        return summary;
    }
}


package com.hsf302.hotelmanagementproject.service;

import java.util.Map;

/**
 * Thống kê & Biểu đồ — Separated from main booking flow for performance.
 */
public interface StatisticsService {

    /**
     * Trạng thái phòng: AVAILABLE / BOOKED / MAINTENANCE / DIRTY counts.
     * Returns: { "labels": [...], "data": [...] }
     */
    Map<String, Object> getRoomStatusStats();

    /**
     * Cơ cấu doanh thu theo loại phòng.
     * Returns: { "labels": [...], "data": [...] }
     */
    Map<String, Object> getRevenueByRoomTypeStats();

    /**
     * Tổng quan số liệu cho dashboard cards.
     * Returns: { totalRooms, totalBookings, totalRevenue, totalUsers }
     */
    Map<String, Object> getDashboardSummary();
}


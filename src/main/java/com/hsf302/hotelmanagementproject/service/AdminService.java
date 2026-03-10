package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.Role;

import java.util.List;

public interface AdminService {

    // ===== Quản lý phòng =====
    List<Room> getAllActiveRooms();
    Room getRoomById(Long id);
    Room saveRoom(Room room);
    void softDeleteRoom(Long id);

    // ===== Quản lý người dùng =====
    List<User> getAllUsers();
    List<User> getUsersByRole(Role role);
    User createUser(String email, String password, String fullName, Role role);
}


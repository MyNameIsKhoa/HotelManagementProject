package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.Role;
import com.hsf302.hotelmanagementproject.repository.RoomRepository;
import com.hsf302.hotelmanagementproject.repository.UserRepository;
import com.hsf302.hotelmanagementproject.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    // ===== Quản lý phòng =====

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllActiveRooms() {
        return roomRepository.findByIsDeletedFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
        if (Boolean.TRUE.equals(room.getIsDeleted())) {
            throw new RuntimeException("Phòng đã bị xóa");
        }
        return room;
    }

    @Override
    public Room saveRoom(Room room) {
        if (room.getIsDeleted() == null) {
            room.setIsDeleted(false);
        }
        return roomRepository.save(room);
    }

    @Override
    public void softDeleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
        room.setIsDeleted(true);
        roomRepository.save(room);
    }

    // ===== Quản lý người dùng =====

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRoleOrderByCreatedAtDesc(role);
    }

    @Override
    public User createUser(String email, String password, String fullName, Role role) {
        // Chỉ cho phép tạo STAFF hoặc GUEST
        if (role == Role.ADMIN) {
            throw new IllegalArgumentException("Không thể tạo tài khoản Admin từ giao diện. Tài khoản Admin phải được tạo trực tiếp trong cơ sở dữ liệu.");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email đã tồn tại: " + email);
        }

        User user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(password);
        user.setFullName(fullName.trim());
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}


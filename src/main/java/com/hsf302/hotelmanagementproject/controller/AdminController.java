package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.Room;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.Role;
import com.hsf302.hotelmanagementproject.entity.enums.RoomStatus;
import com.hsf302.hotelmanagementproject.repository.RoomTypeRepository;
import com.hsf302.hotelmanagementproject.service.AdminService;
import com.hsf302.hotelmanagementproject.service.StaffService;
import com.hsf302.hotelmanagementproject.service.StatisticsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final StatisticsService statisticsService;
    private final StaffService staffService;
    private final RoomTypeRepository roomTypeRepository;

    // ==================== GUARD ====================

    private User requireAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != Role.ADMIN) return null;
        return user;
    }

    // ==================== DASHBOARD ====================

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        model.addAttribute("summary", statisticsService.getDashboardSummary());
        return "admin/dashboard";
    }

    // ==================== QUẢN LÝ PHÒNG ====================

    @GetMapping("/rooms")
    public String roomList(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        model.addAttribute("rooms", adminService.getAllActiveRooms());
        return "admin/rooms";
    }

    @GetMapping("/rooms/new")
    public String newRoomForm(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        model.addAttribute("room", new Room());
        model.addAttribute("roomTypes", roomTypeRepository.findAll());
        model.addAttribute("statuses", new RoomStatus[]{RoomStatus.AVAILABLE, RoomStatus.MAINTENANCE});
        return "admin/room_form";
    }

    @GetMapping("/rooms/edit/{id}")
    public String editRoomForm(@PathVariable Long id, HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        model.addAttribute("room", adminService.getRoomById(id));
        model.addAttribute("roomTypes", roomTypeRepository.findAll());
        model.addAttribute("statuses", new RoomStatus[]{RoomStatus.AVAILABLE, RoomStatus.MAINTENANCE});
        return "admin/room_form";
    }

    @PostMapping("/rooms/save")
    public String saveRoom(@RequestParam String roomNumber,
                           @RequestParam Long roomTypeId,
                           @RequestParam RoomStatus status,
                           @RequestParam(required = false) Long roomId,
                           HttpSession session,
                           RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        try {
            Room room;
            if (roomId != null) {
                room = adminService.getRoomById(roomId);
            } else {
                room = new Room();
                room.setIsDeleted(false);
            }
            room.setRoomNumber(roomNumber);
            room.setRoomType(roomTypeRepository.findById(roomTypeId)
                    .orElseThrow(() -> new RuntimeException("Loại phòng không hợp lệ")));
            room.setStatus(status);
            adminService.saveRoom(room);
            ra.addFlashAttribute("success", "Lưu phòng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        try {
            adminService.softDeleteRoom(id);
            ra.addFlashAttribute("success", "Đã xóa phòng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    // ==================== QUẢN LÝ NGƯỜI DÙNG ====================

    @GetMapping("/users")
    public String userList(@RequestParam(required = false) String role,
                           HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        if (role != null && !role.isBlank()) {
            try {
                model.addAttribute("users", adminService.getUsersByRole(Role.valueOf(role)));
            } catch (IllegalArgumentException e) {
                model.addAttribute("users", adminService.getAllUsers());
            }
        } else {
            model.addAttribute("users", adminService.getAllUsers());
        }
        model.addAttribute("selectedRole", role);
        return "admin/users";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String fullName,
                             @RequestParam Role role,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        try {
            adminService.createUser(email, password, fullName, role);
            ra.addFlashAttribute("success", "Tạo tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ==================== XEM ĐẶT PHÒNG (read-only) ====================

    @GetMapping("/bookings")
    public String bookingList(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/auth/login";
        model.addAttribute("bookings", staffService.getAllBookings());
        model.addAttribute("now", LocalDateTime.now());
        return "admin/bookings";
    }
}


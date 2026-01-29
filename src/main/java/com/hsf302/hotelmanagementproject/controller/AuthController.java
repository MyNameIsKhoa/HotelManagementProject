package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.Role;
import com.hsf302.hotelmanagementproject.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Optional<User> u = authService.authenticate(email, password);

        if (u.isEmpty()) {
            model.addAttribute("error", "Invalid email or password");
            return "auth/login";
        }

        User user = u.get();
        session.setAttribute("currentUser", user);

        // ===== REDIRECT THEO ROLE =====
        if (user.getRole() == Role.STAFF || user.getRole() == Role.ADMIN) {
            return "redirect:/staff";
        }

        return "redirect:/";
    }


    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String fullName,
            HttpSession session,
            Model model
    ) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (password == null /*|| password.length() < 6*/) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }
            User user = authService.register(email, password, fullName);
            session.setAttribute("currentUser", user);
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("fullName", fullName);
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

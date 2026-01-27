package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.enums.Role;
import com.hsf302.hotelmanagementproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> authenticate(String email, String password) {
        if (email == null || email.isBlank() || password == null) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(email)
                .filter(u -> password.equals(u.getPassword()));
    }

    @Override
    @Transactional
    public User register(String email, String password, String fullName) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        String emailLower = email.toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(emailLower)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User u = new User();
        u.setEmail(emailLower);
        u.setPassword(password); // NOTE: hash passwords in production
        u.setFullName(fullName);
        u.setRole(Role.GUEST);
        u.setCreatedAt(LocalDateTime.now());

        return userRepository.save(u);
    }
}

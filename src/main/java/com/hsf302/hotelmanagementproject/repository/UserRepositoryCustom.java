package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}

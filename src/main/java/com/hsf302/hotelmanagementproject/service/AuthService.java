package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.User;

import java.util.Optional;

public interface AuthService {
    Optional<User> authenticate(String email, String password);
    User register(String email, String password, String fullName) throws IllegalArgumentException;



}

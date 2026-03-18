package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    Wallet getOrCreateWallet(User user);

    void refundDeposit(Long bookingId, User approver);


}

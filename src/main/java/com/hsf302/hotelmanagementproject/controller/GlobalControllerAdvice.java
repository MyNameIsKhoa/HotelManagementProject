package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.Wallet;
import com.hsf302.hotelmanagementproject.service.WalletService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final WalletService walletService;

    @ModelAttribute("walletBalance")
    public BigDecimal addWalletBalance(HttpSession session) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return BigDecimal.ZERO;
        }

        Wallet wallet = walletService.getOrCreateWallet(user);
        return wallet.getBalance();
    }
}

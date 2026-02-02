package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.Wallet;
import com.hsf302.hotelmanagementproject.repository.WalletRepository;
import com.hsf302.hotelmanagementproject.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    wallet.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(wallet);
                });
    }

    @Override
    public void refund(User user, BigDecimal amount, Long bookingId) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Wallet wallet = getOrCreateWallet(user);

        wallet.setBalance(wallet.getBalance().add(amount));

        walletRepository.save(wallet);

        System.out.println(">>> REFUND SUCCESS | USER="
                + user.getUserId()
                + " | AMOUNT="
                + amount
                + " | BALANCE="
                + wallet.getBalance());
    }
}

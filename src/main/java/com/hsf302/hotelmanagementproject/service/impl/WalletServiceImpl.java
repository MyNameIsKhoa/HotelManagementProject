package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.entity.Booking;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.Wallet;
import com.hsf302.hotelmanagementproject.entity.WalletTransaction;
import com.hsf302.hotelmanagementproject.entity.enums.Role;
import com.hsf302.hotelmanagementproject.entity.enums.TransactionType;
import com.hsf302.hotelmanagementproject.repository.BookingRepository;
import com.hsf302.hotelmanagementproject.repository.WalletRepository;
import com.hsf302.hotelmanagementproject.repository.WalletTransactionRepository;
import com.hsf302.hotelmanagementproject.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final BookingRepository bookingRepository;

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
    public void refundDeposit(Long bookingId, User approver) {

        if (approver == null) {
            throw new RuntimeException("User not logged in");
        }


        if (approver.getRole() != Role.ADMIN &&
                approver.getRole() != Role.STAFF) {
            throw new RuntimeException("No permission");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        BigDecimal deposit = booking.getDepositAmount();

        if (deposit == null || deposit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("No deposit to refund");
        }

        Wallet wallet = getOrCreateWallet(booking.getUser());

        wallet.setBalance(wallet.getBalance().add(deposit));
        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setAmount(deposit);
        transaction.setType(TransactionType.REFUND);
        transaction.setDescription("Refund deposit booking " + bookingId);
        transaction.setReferenceId(bookingId);

        walletTransactionRepository.save(transaction);
    }
}
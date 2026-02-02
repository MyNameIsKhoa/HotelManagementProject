package com.hsf302.hotelmanagementproject.repository;


import com.hsf302.hotelmanagementproject.entity.WalletTransaction;
import com.hsf302.hotelmanagementproject.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    // Lấy lịch sử giao dịch theo ví
    List<WalletTransaction> findByWallet(Wallet wallet);
}


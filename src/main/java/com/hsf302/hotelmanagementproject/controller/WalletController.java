package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.entity.Wallet;
import com.hsf302.hotelmanagementproject.service.WalletService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/wallet")
    public String walletPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/auth/login";
        }


        Wallet wallet = walletService.getOrCreateWallet(user);

        model.addAttribute("balance", wallet.getBalance());

        return "wallet/wallet";
    }
}

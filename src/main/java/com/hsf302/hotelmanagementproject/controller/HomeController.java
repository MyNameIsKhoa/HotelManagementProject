package com.hsf302.hotelmanagementproject.controller;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
// test//
    /**
     * Trang chủ
     * GET /
     */
    @GetMapping("/")
    public String home() {
        // Chỉ render view để test Thymeleaf
        return "home/index";
    }

    /**
     * Test search submit
     * GET /search
     */
    @GetMapping("/search")
    public String search(
            @RequestParam("checkinDate") String checkinDate,
            @RequestParam("checkoutDate") String checkoutDate,
            Model model
    ) {
        // Tạm thời chỉ show lại dữ liệu để test
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);

        return "home/search_result";
    }
}

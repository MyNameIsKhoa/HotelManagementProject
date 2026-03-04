package com.hsf302.hotelmanagementproject.service;

import com.hsf302.hotelmanagementproject.entity.User;

import java.util.List;
import java.util.Map;

public interface ChatbotService {

    /**
     * Xử lý tin nhắn từ user, query DB lấy context, gọi Gemini API trả về câu trả lời.
     *
     * @param userMessage   câu hỏi của user
     * @param currentUser   user hiện tại (có thể null nếu chưa đăng nhập)
     * @param chatHistory   lịch sử chat trong session (multi-turn)
     * @return              câu trả lời từ AI
     */
    String chat(String userMessage, User currentUser, List<Map<String, String>> chatHistory);

    /**
     * Trả về danh sách gợi ý câu hỏi mẫu
     */
    List<String> getSuggestions();
}


package com.hsf302.hotelmanagementproject.controller;

import com.hsf302.hotelmanagementproject.DTO.ChatRequest;
import com.hsf302.hotelmanagementproject.DTO.ChatResponse;
import com.hsf302.hotelmanagementproject.entity.User;
import com.hsf302.hotelmanagementproject.service.ChatbotService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    private static final String CHAT_HISTORY_KEY = "chatHistory";

    /**
     * POST /api/chatbot - Gửi tin nhắn chat
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request, HttpSession session) {
        String userMessage = request.getMessage();
        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest().body(new ChatResponse("Vui lòng nhập tin nhắn."));
        }

        // Lấy user hiện tại (có thể null)
        User currentUser = (User) session.getAttribute("currentUser");

        // Lấy chat history từ session
        @SuppressWarnings("unchecked")
        List<Map<String, String>> chatHistory = (List<Map<String, String>>) session.getAttribute(CHAT_HISTORY_KEY);
        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
        }

        // Gọi service
        String reply = chatbotService.chat(userMessage, currentUser, chatHistory);

        // Lưu vào chat history (giữ tối đa 20 tin nhắn = 10 cặp user/model)
        chatHistory.add(Map.of("role", "user", "text", userMessage));
        chatHistory.add(Map.of("role", "model", "text", reply));

        // Trim nếu quá 20
        if (chatHistory.size() > 20) {
            chatHistory = new ArrayList<>(chatHistory.subList(chatHistory.size() - 20, chatHistory.size()));
        }

        session.setAttribute(CHAT_HISTORY_KEY, chatHistory);

        return ResponseEntity.ok(new ChatResponse(reply));
    }

    /**
     * GET /api/chatbot/suggestions - Lấy gợi ý câu hỏi
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> suggestions() {
        return ResponseEntity.ok(chatbotService.getSuggestions());
    }

    /**
     * DELETE /api/chatbot/history - Xóa lịch sử chat
     */
    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory(HttpSession session) {
        session.removeAttribute(CHAT_HISTORY_KEY);
        return ResponseEntity.ok().build();
    }
}


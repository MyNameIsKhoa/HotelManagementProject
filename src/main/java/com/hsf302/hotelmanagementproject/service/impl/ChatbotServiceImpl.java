package com.hsf302.hotelmanagementproject.service.impl;

import com.hsf302.hotelmanagementproject.entity.*;
import com.hsf302.hotelmanagementproject.repository.*;
import com.hsf302.hotelmanagementproject.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String model;

    private final RestClient restClient = RestClient.create();

    // ======================== PUBLIC ========================

    @Override
    public String chat(String userMessage, User currentUser, List<Map<String, String>> chatHistory) {
        try {
            // 1. Build context data từ DB
            String dbContext = buildDatabaseContext();

            // 2. Build user context nếu đã đăng nhập
            String userContext = buildUserContext(currentUser);

            // 3. Build system instruction
            String systemInstruction = buildSystemPrompt(dbContext, userContext);

            // 4. Build request body với chat history (multi-turn)
            Map<String, Object> requestBody = buildGeminiRequest(systemInstruction, userMessage, chatHistory);

            // 5. Gọi Gemini API
            String url = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                    model, apiKey
            );

            Map response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // 6. Parse response
            return extractReply(response);

        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini API: ", e);
            return "Xin lỗi, tôi đang gặp sự cố kỹ thuật. Vui lòng thử lại sau hoặc liên hệ lễ tân để được hỗ trợ. 🙏";
        }
    }

    @Override
    public List<String> getSuggestions() {
        return List.of(
                "Phòng nào phù hợp cho gia đình 4 người?",
                "Phòng giá rẻ nhất là phòng nào?",
                "Có phòng nào view đẹp không?",
                "So sánh các loại phòng giúp tôi"
        );
    }

    // ======================== PRIVATE - BUILD CONTEXT ========================

    /**
     * Query DB lấy toàn bộ thông tin phòng, khách sạn, giá, review
     */
    private String buildDatabaseContext() {
        StringBuilder sb = new StringBuilder();

        // Lấy tất cả hotels
        List<Hotel> hotels = hotelRepository.findAll();

        // Lấy tất cả room types
        List<RoomType> roomTypes = roomTypeRepository.findAll();

        // Lấy average rating
        Map<Long, double[]> ratingMap = new HashMap<>(); // roomTypeId -> [avgRating, count]
        try {
            List<Object[]> ratings = reviewRepository.findAverageRatingByRoomType();
            for (Object[] row : ratings) {
                Long rtId = (Long) row[0];
                Double avg = row[1] != null ? (Double) row[1] : 0.0;
                Long count = (Long) row[2];
                ratingMap.put(rtId, new double[]{avg, count});
            }
        } catch (Exception e) {
            log.warn("Không lấy được review data: {}", e.getMessage());
        }

        sb.append("=== DỮ LIỆU KHÁCH SẠN ===\n\n");

        for (Hotel hotel : hotels) {
            sb.append(String.format("🏨 Khách sạn: %s\n", hotel.getName()));
            sb.append(String.format("   Địa chỉ: %s\n", hotel.getAddress()));
            sb.append(String.format("   SĐT: %s\n\n", hotel.getPhone()));

            // Lấy room types thuộc hotel này
            List<RoomType> hotelRooms = roomTypes.stream()
                    .filter(rt -> rt.getHotel() != null && rt.getHotel().getHotelId().equals(hotel.getHotelId()))
                    .toList();

            for (RoomType rt : hotelRooms) {
                sb.append(String.format("   📌 Loại phòng: %s (ID: %d)\n", rt.getName(), rt.getRoomTypeId()));
                sb.append(String.format("      Mô tả: %s\n", rt.getDescription()));
                sb.append(String.format("      Giá cơ bản: %s VNĐ/đêm\n", formatPrice(rt.getBasePrice())));
                sb.append(String.format("      Sức chứa: %d người\n", rt.getCapacity()));
                sb.append(String.format("      Tổng số phòng: %d\n", rt.getTotalRooms()));

                double[] rating = ratingMap.get(rt.getRoomTypeId());
                if (rating != null) {
                    sb.append(String.format("      Đánh giá TB: %.1f/5 (%d lượt)\n", rating[0], (int) rating[1]));
                } else {
                    sb.append("      Đánh giá: Chưa có review\n");
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Build context cá nhân cho user đã đăng nhập (lịch sử booking)
     */
    private String buildUserContext(User currentUser) {
        if (currentUser == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n=== THÔNG TIN KHÁCH HÀNG ===\nTên: %s\n", currentUser.getFullName()));

        try {
            List<Booking> bookings = bookingRepository.findByUserUserId(currentUser.getUserId());
            if (!bookings.isEmpty()) {
                sb.append(String.format("Số lần đặt phòng trước: %d\n", bookings.size()));
                // Lấy loại phòng hay đặt nhất
                Map<String, Long> roomTypeCount = bookings.stream()
                        .filter(b -> b.getRoomType() != null)
                        .collect(Collectors.groupingBy(
                                b -> b.getRoomType().getName(),
                                Collectors.counting()
                        ));
                if (!roomTypeCount.isEmpty()) {
                    String favorite = roomTypeCount.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse("N/A");
                    sb.append(String.format("Loại phòng hay đặt nhất: %s\n", favorite));
                }
            }
        } catch (Exception e) {
            log.warn("Không lấy được booking history: {}", e.getMessage());
        }

        return sb.toString();
    }

    // ======================== PRIVATE - GEMINI API ========================

    /**
     * Build system prompt chứa dữ liệu DB + quy tắc trả lời
     */
    private String buildSystemPrompt(String dbContext, String userContext) {
        return """
                Bạn là trợ lý AI của hệ thống khách sạn "Green Diamond Hotel".
                
                NHIỆM VỤ:
                - Tư vấn, gợi ý phòng khách sạn phù hợp cho khách dựa trên dữ liệu thực từ hệ thống.
                - Trả lời các câu hỏi liên quan đến phòng, giá cả, tiện nghi, vị trí khách sạn.
                - Nếu khách hỏi về vấn đề không liên quan đến khách sạn, nhẹ nhàng dẫn dắt về chủ đề chính.
                
                QUY TẮC:
                1. CHỈ recommend phòng có trong dữ liệu bên dưới, KHÔNG bịa thông tin.
                2. Trả lời bằng tiếng Việt, thân thiện, chuyên nghiệp.
                3. Khi recommend phòng, LUÔN kèm: tên phòng, giá, sức chứa, khách sạn.
                4. Nếu khách chưa cho đủ thông tin (số người, ngân sách...), hỏi lại lịch sự.
                5. Format câu trả lời dễ đọc, dùng emoji phù hợp.
                6. Giữ câu trả lời ngắn gọn, tối đa 300 từ.
                
                """ + dbContext + userContext;
    }

    /**
     * Build request body cho Gemini API hỗ trợ multi-turn conversation
     */
    private Map<String, Object> buildGeminiRequest(String systemInstruction, String userMessage,
                                                    List<Map<String, String>> chatHistory) {
        Map<String, Object> body = new LinkedHashMap<>();

        // System instruction
        Map<String, Object> systemInstr = Map.of(
                "parts", List.of(Map.of("text", systemInstruction))
        );
        body.put("system_instruction", systemInstr);

        // Build contents array với chat history
        List<Map<String, Object>> contents = new ArrayList<>();

        // Thêm lịch sử chat (giới hạn 10 tin gần nhất)
        if (chatHistory != null) {
            int start = Math.max(0, chatHistory.size() - 10);
            for (int i = start; i < chatHistory.size(); i++) {
                Map<String, String> msg = chatHistory.get(i);
                contents.add(Map.of(
                        "role", msg.get("role"),
                        "parts", List.of(Map.of("text", msg.get("text")))
                ));
            }
        }

        // Thêm tin nhắn hiện tại
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));

        body.put("contents", contents);

        // Generation config
        body.put("generationConfig", Map.of(
                "temperature", 0.7,
                "topP", 0.95,
                "maxOutputTokens", 1024
        ));

        return body;
    }

    /**
     * Parse response từ Gemini API
     */
    @SuppressWarnings("unchecked")
    private String extractReply(Map response) {
        if (response == null) {
            return "Không nhận được phản hồi từ AI. Vui lòng thử lại.";
        }

        try {
            List<Map> candidates = (List<Map>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "AI không thể tạo phản hồi. Vui lòng thử lại.";
            }

            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            log.error("Lỗi parse Gemini response: {}", response, e);
            return "Có lỗi xử lý phản hồi. Vui lòng thử lại.";
        }
    }

    // ======================== UTILS ========================

    private String formatPrice(BigDecimal price) {
        if (price == null) return "N/A";
        return String.format("%,.0f", price);
    }
}



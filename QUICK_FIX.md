# 🔴 KHẮC PHỤC LỖI 500 - NHANH CHÓNG

## Vấn đề
Lỗi 500 khi click "Search Rooms" với URL:
```
search?checkinDate=2026-01-29T19%3A59&checkoutDate=2026-01-30T19%3A59
```

## Nguyên nhân chính
- JPQL query dùng Text Blocks (`"""`) cần Java 15+
- System đang compile với Java 8 nhưng Spring Boot 4.0 cần Java 17+

## ✅ ĐÃ SỬA

### 1. RoomTypeRepository.java
- Đổi từ text blocks `"""` sang string concatenation `+`
- Compatible với mọi Java version

### 2. SearchServiceImpl.java  
- Thêm logic lấy thumbnail cho mỗi RoomType
- Trả về: `[RoomType, count, thumbnail]`

### 3. application.properties
- Fix encoding issue (ký tự tiếng Việt bị lỗi)

## 🚀 CÁCH CHẠY (QUAN TRỌNG!)

### ✅ CÁCH 1: Chạy từ IDE (KHUYẾN NGHỊ)

**IntelliJ IDEA:**
1. Mở project
2. Đảm bảo SDK là Java 17 hoặc 21
3. Click vào `HotelManagementProjectApplication.java`
4. Click nút ▶️ Run

**Eclipse:**
1. Import project
2. Right-click project → Properties → Java Build Path
3. Đảm bảo JRE là 17+
4. Right-click `HotelManagementProjectApplication.java` → Run As → Java Application

### ⚠️ CÁCH 2: Maven command (Cần cài Java 17+)

```powershell
# Cài Java 17 hoặc 21 trước
# Download từ: https://adoptium.net/

# Set JAVA_HOME
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"

# Verify
java -version  # Phải là 17+ hoặc 21+

# Run
cd D:\HSF302\HotelManagementProject
.\mvnw.cmd spring-boot:run
```

## 📋 CHECKLIST

- [ ] SQL Server đang chạy
- [ ] Database `hotel_booking_system` đã tạo
- [ ] Tables đã được tạo (hotels, room_types, rooms, room_images)
- [ ] Java SDK là 17 hoặc 21
- [ ] IDE đã sync/reload project

## 🧪 KIỂM TRA

1. **Truy cập:** http://localhost:7777
2. **Chọn ngày check-in và check-out**
3. **Click "Search Rooms"**
4. **✅ Không còn lỗi 500**
5. **✅ Hiển thị danh sách phòng với ảnh**

## 🆘 NẾU VẪN LỖI

### Check Java version trong IDE:
**IntelliJ:**
- File → Project Structure → Project
- SDK phải là 17 hoặc 21

**Eclipse:**
- Window → Preferences → Java → Installed JREs
- Phải có JRE 17 hoặc 21

### Xem log chi tiết:
Khi chạy từ IDE, console sẽ hiện log chi tiết. Copy lỗi và check.

---

**💡 TIP: Chạy từ IDE là cách NHANHdễ và CHÍNH XÁC nhất!**

Nếu không muốn cài Java mới, hãy xem file `FIX_500_ERROR.md` để biết cách downgrade Spring Boot.

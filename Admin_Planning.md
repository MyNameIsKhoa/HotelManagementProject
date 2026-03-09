# Admin Module — Source of Truth

> **Last Updated**: 2026-03-09
> **Status**: IMPLEMENTED

---

## 0. Key Decisions

| # | Decision | Rationale |
|---|----------|-----------|
| 1 | ADMIN accounts created **directly in database** (SQL/DataInitializer) | No self-service admin creation |
| 2 | Admin UI creates only **STAFF** and **GUEST** accounts | Admin manages staff; guests self-register |
| 3 | `/admin/bookings` is **read-only** | Admin observes, staff operates |
| 4 | Room images belong to `RoomType`, no upload on Room form | Room is a physical unit |
| 5 | **Bootstrap 5 CDN + Chart.js CDN** | Same stack as staff/dashboard.html |
| 6 | No Spring Security — **manual session role check** | School project |
| 7 | All UI strings in **Vietnamese** | Project requirement |

---

## 1. Files Created/Modified

### Created (10 files)
| File | Purpose |
|------|---------|
| `service/AdminService.java` | Interface: room CRUD + user management |
| `service/impl/AdminServiceImpl.java` | Implementation with @Transactional |
| `service/impl/StatisticsServiceImpl.java` | Room status stats + revenue stats + summary |
| `controller/AdminController.java` | Page endpoints `/admin/**` |
| `controller/AdminApiController.java` | JSON API `/admin/api/stats/**` for Chart.js |
| `templates/admin/dashboard.html` | Summary cards + 2 pie charts |
| `templates/admin/rooms.html` | Room list with CRUD |
| `templates/admin/room_form.html` | Create/edit room form |
| `templates/admin/users.html` | User list + create form |
| `templates/admin/bookings.html` | Read-only booking list |
| `static/css/admin.css` | Minimal admin styling |

### Modified (3 files)
| File | Change |
|------|--------|
| `repository/UserRepository.java` | +3 queries: findAllByOrderByCreatedAtDesc, findByRole, countByRole |
| `controller/AuthController.java` | ADMIN → `/admin` redirect (was `/staff`) |
| `controller/StaffController.java` | Added session role guard (STAFF + ADMIN) |

### Bug Fixes (pre-existing issues fixed during implementation)
| File | Fix |
|------|-----|
| `config/DataInitializer.java` | Room constructor updated for new `isDeleted` field (5th arg) |
| `pom.xml` | Lombok scope fixed (`annotationProcessor` → `optional`), annotation processor path added |
| `pom.xml` | Java source/target fixed (`6` → `21`) |
| `pom.xml` | Added `project.build.sourceEncoding=UTF-8` |
| `application.properties` | Replaced garbled Vietnamese comments with clean ASCII |

### Previously Modified (from earlier branch work)
| File | Change |
|------|--------|
| `entity/Room.java` | Added `isDeleted` boolean field |
| `repository/RoomRepository.java` | Added `findByIsDeletedFalse()`, `countRoomsByStatus()`, `countByStatusAndIsDeletedFalse()` |
| `repository/BookingRepository.java` | Added `sumRevenueByRoomType()`, `sumTotalRevenue()` |
| `service/StatisticsService.java` | Interface with 3 methods |

---

## 2. Data Flow

### Pie Chart — Trạng thái phòng
```
Chart.js → GET /admin/api/stats/room-status
→ AdminApiController (role guard)
→ StatisticsServiceImpl.getRoomStatusStats()
→ RoomRepository.countRoomsByStatus()
→ SQL: SELECT status, COUNT(*) FROM rooms WHERE is_deleted=false GROUP BY status
→ Map: AVAILABLE→"Trống", BOOKED→"Đã đặt", MAINTENANCE→"Đang sửa chữa", DIRTY→"Chờ dọn"
→ JSON: {"labels":["Trống","Đã đặt",...],"data":[7,3,...]}
```

### Pie Chart — Cơ cấu doanh thu
```
Chart.js → GET /admin/api/stats/revenue
→ AdminApiController (role guard)
→ StatisticsServiceImpl.getRevenueByRoomTypeStats()
→ BookingRepository.sumRevenueByRoomType()
→ SQL: SELECT room_type.name, SUM(total_price) WHERE status IN (CONFIRMED..CHECKED_OUT) GROUP BY name
→ JSON: {"labels":["Deluxe","Family"],"data":[15600000,8000000]}
```

### Room CRUD
```
GET  /admin/rooms/new       → room_form.html (empty)
POST /admin/rooms/save      → AdminService.saveRoom() → redirect /admin/rooms
GET  /admin/rooms/edit/{id} → room_form.html (pre-filled)
POST /admin/rooms/delete/{id} → room.isDeleted=true → redirect /admin/rooms
```

### User Management
```
GET  /admin/users        → list all users (filterable by role)
POST /admin/users/create → validate email unique, role=STAFF|GUEST only → save
```

---

## 3. Role Guard Pattern

### AdminController / AdminApiController — ADMIN only
```java
private User requireAdmin(HttpSession session) {
    User user = (User) session.getAttribute("currentUser");
    if (user == null || user.getRole() != Role.ADMIN) return null;
    return user;
}
```

### StaffController — STAFF + ADMIN
```java
private boolean isStaffOrAdmin(HttpSession session) {
    User user = (User) session.getAttribute("currentUser");
    return user != null && (user.getRole() == Role.STAFF || user.getRole() == Role.ADMIN);
}
```

### AuthController redirect
```java
ADMIN → /admin
STAFF → /staff
GUEST → /
```

---

## 4. URL Mapping

### Pages (AdminController)
| Method | URL | View |
|--------|-----|------|
| GET | `/admin` | admin/dashboard |
| GET | `/admin/rooms` | admin/rooms |
| GET | `/admin/rooms/new` | admin/room_form |
| GET | `/admin/rooms/edit/{id}` | admin/room_form |
| POST | `/admin/rooms/save` | redirect |
| POST | `/admin/rooms/delete/{id}` | redirect |
| GET | `/admin/users` | admin/users |
| POST | `/admin/users/create` | redirect |
| GET | `/admin/bookings` | admin/bookings |

### JSON API (AdminApiController)
| Method | URL | Response |
|--------|-----|----------|
| GET | `/admin/api/stats/room-status` | `{"labels":[...],"data":[...]}` |
| GET | `/admin/api/stats/revenue` | `{"labels":[...],"data":[...]}` |
| GET | `/admin/api/stats/summary` | `{"totalRooms":N,...}` |

---

## 5. Vietnamese Labels

| English | Vietnamese |
|---------|-----------|
| AVAILABLE | Trống |
| BOOKED | Đã đặt |
| MAINTENANCE | Đang sửa chữa |
| DIRTY | Chờ dọn |
| ADMIN | Quản trị viên |
| STAFF | Nhân viên |
| GUEST | Khách hàng |
| PENDING_DEPOSIT | Chờ đặt cọc |
| CONFIRMED | Đã xác nhận |
| ASSIGNED | Đã gán phòng |
| CHECKED_IN | Đã nhận phòng |
| CHECKED_OUT | Đã trả phòng |
| CANCELLED | Đã hủy |

---

## 6. Existing Booking Flow — NOT MODIFIED

```
PENDING_DEPOSIT → CONFIRMED → ASSIGNED → CHECKED_IN → CHECKED_OUT
                                 ↓                        ↓
                          room=BOOKED               room=DIRTY
```

Lives in StaffServiceImpl. Admin only reads.

---

## 7. AdminService Contract

```java
public interface AdminService {
    List<Room> getAllActiveRooms();       // WHERE isDeleted = false
    Room getRoomById(Long id);           // throws if not found/deleted
    Room saveRoom(Room room);            // create or update
    void softDeleteRoom(Long id);        // isDeleted = true

    List<User> getAllUsers();             // ordered by createdAt DESC
    List<User> getUsersByRole(Role role);
    User createUser(email, password, fullName, role);  // STAFF/GUEST only
}
```


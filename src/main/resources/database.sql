/* =========================================================
   HOTEL BOOKING SYSTEM - FULL DATABASE SCRIPT (SQL SERVER)
   ========================================================= */

-- 1. CREATE DATABASE
CREATE DATABASE hotel_booking_system;
GO

USE hotel_booking_system;
GO

/* =========================================================
   2. USERS
   ========================================================= */
CREATE TABLE users (
                       user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                       email NVARCHAR(100) NOT NULL UNIQUE,
                       password NVARCHAR(255) NOT NULL,
                       full_name NVARCHAR(150),
                       role NVARCHAR(20) NOT NULL, -- ADMIN / STAFF / GUEST
                       created_at DATETIME2 DEFAULT SYSDATETIME()
);
GO

/* =========================================================
   3. HOTELS
   ========================================================= */
CREATE TABLE hotels (
                        hotel_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                        name NVARCHAR(150) NOT NULL,
                        address NVARCHAR(255),
                        phone NVARCHAR(20)
);
GO

/* =========================================================
   4. ROOM_TYPES
   ========================================================= */
CREATE TABLE room_types (
                            room_type_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                            hotel_id BIGINT NOT NULL,
                            name NVARCHAR(100) NOT NULL,
                            description NVARCHAR(MAX),
                            base_price DECIMAL(10,2) NOT NULL,
                            capacity INT NOT NULL,
                            total_rooms INT,

                            CONSTRAINT fk_room_type_hotel
                                FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id)
);
GO

/* =========================================================
   5. ROOM_IMAGES
   ========================================================= */
CREATE TABLE room_images (
                             image_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                             room_type_id BIGINT NOT NULL,
                             image_url NVARCHAR(255) NOT NULL,
                             is_thumbnail BIT DEFAULT 0,

                             CONSTRAINT fk_room_image_room_type
                                 FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id)
);
GO

/* =========================================================
   6. ROOMS
   ========================================================= */
CREATE TABLE rooms (
                       room_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                       room_type_id BIGINT NOT NULL,
                       room_number NVARCHAR(10) NOT NULL,
                       status NVARCHAR(20) NOT NULL, -- AVAILABLE / DIRTY / MAINTENANCE

                       CONSTRAINT fk_room_room_type
                           FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id),

                       CONSTRAINT uq_room_number UNIQUE (room_number)
);
GO

/* =========================================================
   7. PRICE_POLICIES
   ========================================================= */
CREATE TABLE price_policies (
                                policy_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                room_type_id BIGINT NOT NULL,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                price_multiplier DECIMAL(5,2) NOT NULL,
                                note NVARCHAR(255),

                                CONSTRAINT fk_price_policy_room_type
                                    FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id)
);
GO

/* =========================================================
   8. BOOKINGS
   ========================================================= */
CREATE TABLE bookings (
                          booking_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          room_type_id BIGINT NOT NULL,
                          room_id BIGINT NULL, -- NULL khi đặt, gán khi check-in
                          checkin_date DATETIME2 NOT NULL,
                          checkout_date DATETIME2 NOT NULL,
                          actual_checkin_time DATETIME2 NULL,
                          actual_checkout_time DATETIME2 NULL,
                          total_price DECIMAL(12,2),
                          status NVARCHAR(30) NOT NULL, -- PENDING / CONFIRMED / CANCELLED / CHECKED_IN / COMPLETED
                          created_at DATETIME2 DEFAULT SYSDATETIME(),

                          CONSTRAINT fk_booking_user
                              FOREIGN KEY (user_id) REFERENCES users(user_id),

                          CONSTRAINT fk_booking_room_type
                              FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id),

                          CONSTRAINT fk_booking_room
                              FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);
GO

/* =========================================================
   9. PAYMENTS
   ========================================================= */
CREATE TABLE payments (
                          payment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                          booking_id BIGINT NOT NULL,
                          amount DECIMAL(12,2) NOT NULL,
                          method NVARCHAR(30) NOT NULL, -- VNPAY / CASH
                          status NVARCHAR(30) NOT NULL, -- SUCCESS / FAILED / REFUNDED
                          transaction_ref NVARCHAR(100),
                          paid_at DATETIME2,

                          CONSTRAINT fk_payment_booking
                              FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);
GO

/* =========================================================
   10. SERVICE_USAGES
   ========================================================= */
CREATE TABLE service_usages (
                                service_usage_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                booking_id BIGINT NOT NULL,
                                service_name NVARCHAR(100) NOT NULL,
                                price DECIMAL(10,2) NOT NULL,
                                quantity INT NOT NULL,
                                used_at DATETIME2 DEFAULT SYSDATETIME(),

                                CONSTRAINT fk_service_usage_booking
                                    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);
GO

/* =========================================================
   11. REVIEWS
   ========================================================= */
CREATE TABLE reviews (
                         review_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                         booking_id BIGINT NOT NULL,
                         rating INT NOT NULL,
                         comment NVARCHAR(MAX),
                         created_at DATETIME2 DEFAULT SYSDATETIME(),

                         CONSTRAINT fk_review_booking
                             FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),

                         CONSTRAINT uq_review_booking UNIQUE (booking_id)
);
GO

/* =========================================================
   12. SEARCH_HISTORIES
   ========================================================= */
CREATE TABLE search_histories (
                                  search_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                  user_id BIGINT NOT NULL,
                                  keyword NVARCHAR(255),
                                  search_time DATETIME2 DEFAULT SYSDATETIME(),
                                  filters NVARCHAR(MAX), -- JSON text

                                  CONSTRAINT fk_search_user
                                      FOREIGN KEY (user_id) REFERENCES users(user_id)
);
GO

/* =========================================================
   13. AI_PROFILES
   ========================================================= */
CREATE TABLE ai_profiles (
                             ai_profile_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             preference_vector NVARCHAR(MAX), -- JSON
                             updated_at DATETIME2 DEFAULT SYSDATETIME(),

                             CONSTRAINT fk_ai_profile_user
                                 FOREIGN KEY (user_id) REFERENCES users(user_id),

                             CONSTRAINT uq_ai_profile_user UNIQUE (user_id)
);
GO

/* =========================================================
   14. RECOMMENDATIONS
   ========================================================= */
CREATE TABLE recommendations (
                                 recommendation_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                 ai_profile_id BIGINT NOT NULL,
                                 room_type_id BIGINT NOT NULL,
                                 score DECIMAL(5,4) NOT NULL,
                                 generated_at DATETIME2 DEFAULT SYSDATETIME(),

                                 CONSTRAINT fk_recommend_ai_profile
                                     FOREIGN KEY (ai_profile_id) REFERENCES ai_profiles(ai_profile_id),

                                 CONSTRAINT fk_recommend_room_type
                                     FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id)
);
GO

-- HOTELS
SET IDENTITY_INSERT hotels ON
INSERT INTO hotels (hotel_id, address, name, phone) VALUES
                                                        (1, 'Da Nang', 'Sunrise Hotel', '090000001'),
                                                        (2, 'Nha Trang', 'Moonlight Resort', '090000002');
SET IDENTITY_INSERT hotels OFF


-- ROOM TYPES
SET IDENTITY_INSERT room_types ON
INSERT INTO room_types (room_type_id, hotel_id, name, description, capacity, base_price, total_rooms) VALUES
                                                                                                          (1, 1, 'Deluxe Room', 'Nice view', 2, 1200000, 3),
                                                                                                          (2, 1, 'Family Room', 'For family', 4, 2000000, 2),
                                                                                                          (3, 2, 'Standard Room', 'Basic', 2, 800000, 4);
SET IDENTITY_INSERT room_types OFF


-- ROOMS
SET IDENTITY_INSERT rooms ON
INSERT INTO rooms (room_id, room_type_id, room_number, status) VALUES
                                                                   (1,1,'D101','AVAILABLE'),
                                                                   (2,1,'D102','BOOKED'),
                                                                   (3,1,'D103','BOOKED'),
                                                                   (4,2,'F201','BOOKED'),
                                                                   (5,2,'F202','AVAILABLE'),
                                                                   (6,3,'S301','AVAILABLE'),
                                                                   (7,3,'S302','AVAILABLE'),
                                                                   (8,3,'S303','AVAILABLE'),
                                                                   (9,3,'S304','AVAILABLE');
SET IDENTITY_INSERT rooms OFF


-- ROOM IMAGES
SET IDENTITY_INSERT room_images ON
INSERT INTO room_images (image_id, room_type_id, image_url, is_thumbnail) VALUES
                                                                              (1,1,'/images/140127103345-peninsula-shanghai-deluxe-mock-up.jpg',1),
                                                                              (2,1,'/images/Sofitel-Dubai-Wafi-Luxury-Room-Bedroom-Skyline-View-Image1_WEB.jpg',0),
                                                                              (3,1,'/images/1.jpg',0),
                                                                              (4,1,'/images/istockphoto-1452529483-612x612.jpg',0),

                                                                              (5,2,'/images/fullsize-82524236.webp',1),
                                                                              (6,2,'/images/heroOVS-Premier-Family_Hotel-Nikko-Bali-2.jpg',0),
                                                                              (7,2,'/images/executive-family-room-main.png',0),
                                                                              (8,2,'/images/family-room-1-700x430.jpg',0),

                                                                              (9,3,'/images/2.jpg',1),
                                                                              (10,3,'/images/3.webp',0),
                                                                              (11,3,'/images/12.jpg',0),
                                                                              (12,3,'/images/4.webp',0);
SET IDENTITY_INSERT room_images OFF
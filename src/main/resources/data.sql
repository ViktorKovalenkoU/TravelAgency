INSERT INTO users (username, password, email, phone_number, role, active, balance, failed_attempts, lock_time)
VALUES
  ('admin', '{bcrypt}$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', 'admin@example.com', '+380501234567', 'ROLE_ADMIN', TRUE, 0.00, 0, NULL);

INSERT INTO users (username, password, email, phone_number, role, active, balance, failed_attempts, lock_time)
VALUES
  ('manager', '{bcrypt}$2a$10$ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ', 'manager@example.com', '+380661234567', 'ROLE_MANAGER', TRUE, 0.00, 0, NULL);

INSERT INTO users (username, password, email, phone_number, role, active, balance, failed_attempts, lock_time)
VALUES
  ('user1', '{bcrypt}$2a$10$YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY', 'user1@example.com', '+380731234567', 'ROLE_USER', TRUE, 0.00, 0, NULL);

INSERT INTO users (username, password, email, phone_number, role, active, balance, failed_attempts, lock_time)
VALUES
  ('user2', '{bcrypt}$2a$10$AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA', 'user2@example.com', '+380801234567', 'ROLE_USER', TRUE, 0.00, 0, NULL);

INSERT INTO users (username, password, email, phone_number, role, active, balance, failed_attempts, lock_time)
VALUES
  ('support', '{bcrypt}$2a$10$BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB', 'support@example.com', '+380911234567', 'ROLE_SUPPORT', TRUE, 0.00, 0, NULL);


INSERT INTO vouchers (title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES (
    'Adventure Package',
    'Experience thrilling adventures and outdoor activities.',
    299.99,
    'Adventure',
    'Helicopter',
    'Luxury',
    'AVAILABLE',
    CURRENT_DATE,
    DATEADD('DAY', 7, CURRENT_DATE),
    (SELECT id FROM users WHERE username = 'manager'),
    FALSE
);

INSERT INTO vouchers (title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES (
    'Relaxation Deal',
    'A rejuvenating getaway with spa treatments and wellness sessions.',
    199.99,
    'Spa',
    'Limousine',
    'Premium',
    'BOOKED',
    CURRENT_DATE,
    DATEADD('DAY', 3, CURRENT_DATE),
    (SELECT id FROM users WHERE username = 'user1'),
    TRUE
);

INSERT INTO vouchers (title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES (
    'Family Special',
    'An ideal vacation package designed for family bonding and fun activities.',
    399.99,
    'Theme Park',
    'Minivan',
    'Family',
    'AVAILABLE',
    CURRENT_DATE,
    DATEADD('DAY', 10, CURRENT_DATE),
    (SELECT id FROM users WHERE username = 'user2'),
    FALSE
);

INSERT INTO vouchers (title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES (
    'Weekend Escape',
    'A short, refreshing break to recharge over the weekend.',
    149.99,
    'Weekend Tour',
    'Train',
    'Standard',
    'ACTIVE',
    CURRENT_DATE,
    DATEADD('DAY', 2, CURRENT_DATE),
    (SELECT id FROM users WHERE username = 'support'),
    TRUE
);
INSERT INTO users (username, password, email, role)
VALUES
  ('admin',    '{bcrypt}$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', 'admin@example.com',    'ROLE_ADMIN'),
  ('manager',  '{bcrypt}$2a$10$ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ', 'manager@example.com',  'ROLE_MANAGER'),
  ('user1',    '{bcrypt}$2a$10$YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY', 'user1@example.com',    'ROLE_USER'),
  ('user2',    '{bcrypt}$2a$10$AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA', 'user2@example.com',    'ROLE_USER'),
  ('support',  '{bcrypt}$2a$10$BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB', 'support@example.com',  'ROLE_SUPPORT');

INSERT INTO vouchers
  (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES
  ('b91d3f64-d3b4-4e4b-aaaa-bee24c63a346',
   'Adventure Package',
   'Experience thrilling adventures and outdoor activities.',
   299.99,
   'Adventure',
   'Helicopter',
   'Luxury',
   'AVAILABLE',
   CURRENT_DATE,
   DATEADD('DAY', 7, CURRENT_DATE),
   2,
   FALSE);

INSERT INTO vouchers
  (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES
  ('c02e4f75-d4c5-4e5c-bbbb-bee24c63a347',
   'Relaxation Deal',
   'A rejuvenating getaway with spa treatments and wellness sessions.',
   199.99,
   'Spa',
   'Limousine',
   'Premium',
   'BOOKED',
   CURRENT_DATE,
   DATEADD('DAY', 3, CURRENT_DATE),
   3,
   TRUE);

INSERT INTO vouchers
  (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES
  ('d13f5f86-d5d6-4e6d-cccc-bee24c63a348',
   'Family Special',
   'An ideal vacation package designed for family bonding and fun activities.',
   399.99,
   'Theme Park',
   'Minivan',
   'Family',
   'AVAILABLE',
   CURRENT_DATE,
   DATEADD('DAY', 10, CURRENT_DATE),
   4,
   FALSE);

INSERT INTO vouchers
  (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, is_hot)
VALUES
  ('e24f6f97-d6e7-4e7e-dddd-bee24c63a349',
   'Weekend Escape',
   'A short, refreshing break to recharge over the weekend.',
   149.99,
   'Weekend Tour',
   'Train',
   'Standard',
   'ACTIVE',
   CURRENT_DATE,
   DATEADD('DAY', 2, CURRENT_DATE),
   5,
   TRUE);

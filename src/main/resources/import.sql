-- import.sql
-- This file is used for H2 database initialization in development

-- Seed data for users
INSERT INTO users (username, email, password, role, created_at, updated_at) VALUES
                                                                                ('admin', 'admin@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                ('user1', 'user1@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                ('premium1', 'premium1@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'PREMIUM_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed data for products
INSERT INTO products (name, description, price, quantity, deleted, created_at, updated_at) VALUES
                                                                                               ('Laptop Pro 15', 'High-performance laptop with 15-inch display', 1299.99, 50, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 29.99, 150, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 89.99, 75, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('4K Monitor', '27-inch 4K IPS monitor with HDR support', 399.99, 30, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('USB-C Hub', '7-in-1 USB-C hub with multiple ports', 49.99, 200, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('Webcam HD', '1080p HD webcam with auto-focus', 69.99, 80, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('Desk Lamp', 'LED desk lamp with adjustable brightness', 34.99, 100, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('External SSD 1TB', 'Portable 1TB SSD with USB 3.2', 129.99, 60, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('Noise Cancelling Headphones', 'Premium headphones with active noise cancellation', 199.99, 40, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                               ('Standing Desk', 'Electric height-adjustable standing desk', 449.99, 20, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
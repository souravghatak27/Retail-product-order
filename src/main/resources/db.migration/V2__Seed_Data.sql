-- V2__Seed_Data.sql

-- Insert default users (password: admin123 - BCrypt encoded)
INSERT INTO users (username, email, password, role) VALUES
                                                        ('admin', 'admin@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'ADMIN'),
                                                        ('user1', 'user1@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'USER'),
                                                        ('premium1', 'premium1@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'PREMIUM_USER');

-- Insert sample products
INSERT INTO products (name, description, price, quantity) VALUES
                                                              ('Laptop Pro 15', 'High-performance laptop with 16GB RAM', 1299.99, 50),
                                                              ('Wireless Mouse', 'Ergonomic wireless mouse', 29.99, 200),
                                                              ('Mechanical Keyboard', 'RGB mechanical gaming keyboard', 89.99, 150),
                                                              ('4K Monitor', '27-inch 4K UHD monitor', 399.99, 75),
                                                              ('USB-C Hub', '7-in-1 USB-C hub adapter', 49.99, 300),
                                                              ('Webcam HD', '1080p HD webcam with mic', 79.99, 100),
                                                              ('Desk Lamp', 'LED desk lamp with touch control', 34.99, 180),
                                                              ('Phone Stand', 'Adjustable aluminum phone stand', 19.99, 250),
                                                              ('Laptop Bag', 'Waterproof laptop backpack', 59.99, 120),
                                                              ('Power Bank', '20000mAh portable charger', 39.99, 200),
                                                              ('Bluetooth Headphones', 'Water resistant long duration battery backup', 20.49, 100);
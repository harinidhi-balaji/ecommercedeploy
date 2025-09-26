-- Complete Database Setup Script for ECommerce Application
-- Execute this script to set up the PostgreSQL database from scratch

-- Step 1: Connect to PostgreSQL as superuser and create database
-- psql -U postgres -h localhost
-- CREATE DATABASE mydb;
-- \q

-- Step 2: Connect to the application database
-- psql -U postgres -h localhost -d mydb

-- Create dedicated user for the application (optional but recommended)
-- CREATE USER ecommerce_user WITH PASSWORD 'ecommerce_password';
-- GRANT ALL PRIVILEGES ON DATABASE mydb TO ecommerce_user;

-- Step 3: Execute the schema file
-- Note: You can run the schema.sql file directly or copy its contents here

-- Alternative: Include the schema content directly
-- The following is the same as schema.sql but included here for convenience

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS cart CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop custom types if they exist
DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS order_status CASCADE;

-- Create custom enum types for PostgreSQL
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE order_status AS ENUM ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED');

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role user_role NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create indexes for users table
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0),
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for products table
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_stock ON products(stock_quantity);
CREATE INDEX idx_products_created_at ON products(created_at);

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount > 0),
    status order_status NOT NULL DEFAULT 'PENDING',
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_orders_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for orders table
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);

-- Order Items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    
    CONSTRAINT fk_order_items_order_id FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product_id FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create indexes for order_items table
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- Cart table
CREATE TABLE cart (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    
    CONSTRAINT fk_cart_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_product_id FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_product UNIQUE (user_id, product_id)
);

-- Create indexes for cart table
CREATE INDEX idx_cart_user_id ON cart(user_id);
CREATE INDEX idx_cart_product_id ON cart(product_id);

-- Step 4: Insert initial data

-- Insert default admin user
-- Password is 'admin123' (will be encoded by BCrypt in the application)
INSERT INTO users (username, password, email, role) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'admin@ecommerce.com', 'ADMIN');

-- Insert 25 sample products for better variety
INSERT INTO products (name, description, price, stock_quantity, image_url) VALUES
('Laptop', 'High-performance laptop with 16GB RAM and 512GB SSD', 999.99, 50, 'https://via.placeholder.com/300x200?text=Laptop'),
('Smartphone', 'Latest smartphone with advanced camera and long battery life', 699.99, 100, 'https://via.placeholder.com/300x200?text=Smartphone'),
('Headphones', 'Wireless noise-cancelling headphones', 199.99, 75, 'https://via.placeholder.com/300x200?text=Headphones'),
('Watch', 'Smart watch with fitness tracking features', 299.99, 30, 'https://via.placeholder.com/300x200?text=Smart+Watch'),
('Tablet', '10-inch tablet perfect for work and entertainment', 449.99, 25, 'https://via.placeholder.com/300x200?text=Tablet'),
('Gaming Mouse', 'High-precision gaming mouse with RGB lighting', 79.99, 60, 'https://via.placeholder.com/300x200?text=Gaming+Mouse'),
('Keyboard', 'Mechanical keyboard with customizable keys', 129.99, 40, 'https://via.placeholder.com/300x200?text=Keyboard'),
('Monitor', '27-inch 4K monitor for professional use', 399.99, 20, 'https://via.placeholder.com/300x200?text=Monitor'),
('Webcam', 'HD webcam for video conferencing', 89.99, 80, 'https://via.placeholder.com/300x200?text=Webcam'),
('Speakers', 'Bluetooth speakers with excellent sound quality', 149.99, 35, 'https://via.placeholder.com/300x200?text=Speakers'),
('Graphics Card', 'High-end graphics card for gaming and rendering', 599.99, 15, 'https://via.placeholder.com/300x200?text=Graphics+Card'),
('External HDD', '2TB external hard drive for backup storage', 89.99, 45, 'https://via.placeholder.com/300x200?text=External+HDD'),
('Wireless Charger', 'Fast wireless charger for smartphones', 39.99, 70, 'https://via.placeholder.com/300x200?text=Wireless+Charger'),
('USB Hub', '7-port USB 3.0 hub with fast data transfer', 29.99, 55, 'https://via.placeholder.com/300x200?text=USB+Hub'),
('Router', 'High-speed wireless router for home networking', 179.99, 25, 'https://via.placeholder.com/300x200?text=Router'),
('Power Bank', '20000mAh portable charger with fast charging', 49.99, 90, 'https://via.placeholder.com/300x200?text=Power+Bank'),
('Bluetooth Earbuds', 'True wireless earbuds with noise cancellation', 159.99, 65, 'https://via.placeholder.com/300x200?text=Bluetooth+Earbuds'),
('Docking Station', 'USB-C docking station for laptops', 199.99, 30, 'https://via.placeholder.com/300x200?text=Docking+Station'),
('Printer', 'All-in-one inkjet printer with wireless connectivity', 149.99, 20, 'https://via.placeholder.com/300x200?text=Printer'),
('SSD Drive', '1TB NVMe SSD for high-speed storage', 119.99, 40, 'https://via.placeholder.com/300x200?text=SSD+Drive'),
('Game Controller', 'Wireless game controller for PC and console', 69.99, 50, 'https://via.placeholder.com/300x200?text=Game+Controller'),
('Desk Lamp', 'LED desk lamp with adjustable brightness', 34.99, 35, 'https://via.placeholder.com/300x200?text=Desk+Lamp'),
('Phone Case', 'Protective case for latest smartphone models', 24.99, 100, 'https://via.placeholder.com/300x200?text=Phone+Case'),
('Cable Organizer', 'Desktop cable management system', 19.99, 75, 'https://via.placeholder.com/300x200?text=Cable+Organizer'),
('Laptop Stand', 'Adjustable aluminum laptop stand for ergonomics', 59.99, 45, 'https://via.placeholder.com/300x200?text=Laptop+Stand');

-- Insert sample user (optional)
-- Password is 'user123' (will be encoded by BCrypt in the application)
INSERT INTO users (username, password, email, role) VALUES
('testuser', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'user@test.com', 'USER');

-- Step 5: Create additional indexes for performance
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date DESC);
CREATE INDEX idx_products_stock_price ON products(stock_quantity, price);
CREATE INDEX idx_order_items_order_product ON order_items(order_id, product_id);

-- Step 6: Create views for reporting
CREATE VIEW v_order_summary AS
SELECT
    o.id as order_id,
    u.username,
    u.email,
    o.total_amount,
    o.status,
    o.order_date,
    COUNT(oi.id) as item_count
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, u.username, u.email, o.total_amount, o.status, o.order_date;

CREATE VIEW v_product_sales AS
SELECT
    p.id as product_id,
    p.name as product_name,
    p.price,
    p.stock_quantity,
    COALESCE(SUM(oi.quantity), 0) as total_sold,
    COALESCE(SUM(oi.quantity * oi.price), 0) as total_revenue
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id AND o.status != 'CANCELLED'
GROUP BY p.id, p.name, p.price, p.stock_quantity;

CREATE VIEW v_user_orders AS
SELECT
    u.id as user_id,
    u.username,
    u.email,
    COUNT(o.id) as total_orders,
    COALESCE(SUM(CASE WHEN o.status = 'DELIVERED' THEN o.total_amount ELSE 0 END), 0) as total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.role = 'USER'
GROUP BY u.id, u.username, u.email;

-- Database setup complete!
-- You can now run the Spring Boot application.

-- Step 4: Insert initial data

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email, role) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'admin@ecommerce.com', 'ADMIN');

-- Insert sample test user (password: user123)
INSERT INTO users (username, password, email, role) VALUES
('testuser', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'user@test.com', 'USER');

-- Insert sample products with realistic data
INSERT INTO products (name, description, price, stock_quantity, image_url) VALUES
('MacBook Pro 16"', 'Apple MacBook Pro with M2 Pro chip, 16GB RAM, 512GB SSD. Perfect for professionals and creatives.', 2399.99, 15, 'https://via.placeholder.com/400x300/1f2937/ffffff?text=MacBook+Pro'),

('iPhone 15 Pro', 'Latest iPhone with A17 Pro chip, Pro camera system, and titanium design. Available in multiple colors.', 999.99, 50, 'https://via.placeholder.com/400x300/3b82f6/ffffff?text=iPhone+15+Pro'),

('AirPods Pro (2nd Gen)', 'Active Noise Cancellation, Adaptive Transparency, Personalized Spatial Audio with MagSafe Charging Case.', 249.99, 100, 'https://via.placeholder.com/400x300/ef4444/ffffff?text=AirPods+Pro'),

('Apple Watch Series 9', 'Advanced health monitoring, fitness tracking, and seamless integration with iPhone. GPS + Cellular options available.', 399.99, 75, 'https://via.placeholder.com/400x300/10b981/ffffff?text=Apple+Watch'),

('iPad Air (5th Gen)', '10.9-inch Liquid Retina display with M1 chip. Perfect balance of performance, capability, and portability.', 599.99, 30, 'https://via.placeholder.com/400x300/8b5cf6/ffffff?text=iPad+Air'),

('Logitech MX Master 3S', 'Advanced wireless mouse with ultra-fast scrolling, ergonomic design, and multi-device connectivity.', 99.99, 80, 'https://via.placeholder.com/400x300/f59e0b/ffffff?text=MX+Master+3S'),

('Mechanical Gaming Keyboard', 'RGB backlit mechanical keyboard with Cherry MX switches, programmable keys, and aluminum frame.', 159.99, 45, 'https://via.placeholder.com/400x300/ec4899/ffffff?text=Gaming+Keyboard'),

('Dell UltraSharp 27" 4K', 'Professional 4K monitor with 99% sRGB color accuracy, USB-C connectivity, and ergonomic stand.', 549.99, 25, 'https://via.placeholder.com/400x300/06b6d4/ffffff?text=Dell+Monitor'),

('Logitech C920 HD Webcam', 'Full HD 1080p webcam with auto-focus, built-in microphones, and universal compatibility.', 79.99, 60, 'https://via.placeholder.com/400x300/84cc16/ffffff?text=Webcam'),

('Bose SoundLink Flex', 'Portable Bluetooth speaker with waterproof design, 12-hour battery life, and premium sound quality.', 149.99, 40, 'https://via.placeholder.com/400x300/6366f1/ffffff?text=Bose+Speaker'),

('Samsung Galaxy Tab S9', 'Premium Android tablet with S Pen, 120Hz AMOLED display, and desktop-class performance.', 799.99, 20, 'https://via.placeholder.com/400x300/f97316/ffffff?text=Galaxy+Tab'),

('Sony WH-1000XM5', 'Industry-leading noise canceling wireless headphones with exceptional sound quality and 30-hour battery.', 399.99, 35, 'https://via.placeholder.com/400x300/0ea5e9/ffffff?text=Sony+Headphones'),

('Nintendo Switch OLED', 'Gaming console with 7-inch OLED screen, enhanced audio, and 64GB internal storage. Joy-Con controllers included.', 349.99, 50, 'https://via.placeholder.com/400x300/dc2626/ffffff?text=Nintendo+Switch'),

('Google Pixel Buds Pro', 'Wireless earbuds with Active Noise Cancellation, 11-hour battery life, and Google Assistant integration.', 199.99, 70, 'https://via.placeholder.com/400x300/059669/ffffff?text=Pixel+Buds'),

('Microsoft Surface Pro 9', '2-in-1 laptop and tablet with Intel 12th Gen processors, 13-inch touchscreen, and Type Cover included.', 1299.99, 18, 'https://via.placeholder.com/400x300/7c3aed/ffffff?text=Surface+Pro');

-- Create additional indexes for performance
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date DESC);
CREATE INDEX idx_products_stock_price ON products(stock_quantity, price);
CREATE INDEX idx_order_items_order_product ON order_items(order_id, product_id);

-- Create views for reporting
CREATE VIEW v_order_summary AS
SELECT
    o.id as order_id,
    u.username,
    u.email,
    o.total_amount,
    o.status,
    o.order_date,
    COUNT(oi.id) as item_count
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, u.username, u.email, o.total_amount, o.status, o.order_date;

CREATE VIEW v_product_sales AS
SELECT
    p.id as product_id,
    p.name as product_name,
    p.price,
    p.stock_quantity,
    COALESCE(SUM(oi.quantity), 0) as total_sold,
    COALESCE(SUM(oi.quantity * oi.price), 0) as total_revenue
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id AND o.status != 'CANCELLED'
GROUP BY p.id, p.name, p.price, p.stock_quantity;

CREATE VIEW v_user_orders AS
SELECT
    u.id as user_id,
    u.username,
    u.email,
    COUNT(o.id) as total_orders,
    COALESCE(SUM(CASE WHEN o.status = 'DELIVERED' THEN o.total_amount ELSE 0 END), 0) as total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.role = 'USER'
GROUP BY u.id, u.username, u.email;

-- Verify the setup
SELECT 'Database setup completed successfully!' as message;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as product_count FROM products;

-- Show sample data
SELECT 'Sample Users:' as info;
SELECT username, email, role FROM users;

SELECT 'Sample Products (first 5):' as info;
SELECT name, price, stock_quantity FROM products LIMIT 5;
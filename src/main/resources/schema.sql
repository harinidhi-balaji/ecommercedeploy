-- ECommerce Database Schema
-- PostgreSQL Compatible

-- Create database (run this separately if needed)
-- CREATE DATABASE mydb;
-- \c mydb;

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

-- Insert default admin user
-- Password is 'admin123' (will be encoded by BCrypt in the application)
INSERT INTO users (username, password, email, role) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'admin@ecommerce.com', 'ADMIN');

-- Insert sample products
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
('Speakers', 'Bluetooth speakers with excellent sound quality', 149.99, 35, 'https://via.placeholder.com/300x200?text=Speakers');

-- Insert sample user (optional)
-- Password is 'user123' (will be encoded by BCrypt in the application)
INSERT INTO users (username, password, email, role) VALUES
('testuser', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'user@test.com', 'USER');

-- Create additional indexes for better performance
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date DESC);
CREATE INDEX idx_products_stock_price ON products(stock_quantity, price);
CREATE INDEX idx_order_items_order_product ON order_items(order_id, product_id);

-- Views for reporting (optional)
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
USE ecommerce_db;

-- Insert 25 diverse products for testing
INSERT INTO products (name, description, price, stock_quantity, image_url, created_at) VALUES
('MacBook Pro 16"', 'Apple MacBook Pro with M2 Pro chip, 16GB RAM, 512GB SSD. Perfect for professionals and creatives.', 2399.99, 15, 'https://via.placeholder.com/400x300/1f2937/ffffff?text=MacBook+Pro', NOW()),

('iPhone 15 Pro', 'Latest iPhone with A17 Pro chip, Pro camera system, and titanium design. Available in multiple colors.', 999.99, 50, 'https://via.placeholder.com/400x300/3b82f6/ffffff?text=iPhone+15+Pro', NOW()),

('AirPods Pro (2nd Gen)', 'Active Noise Cancellation, Adaptive Transparency, Personalized Spatial Audio with MagSafe Charging Case.', 249.99, 100, 'https://via.placeholder.com/400x300/ef4444/ffffff?text=AirPods+Pro', NOW()),

('Apple Watch Series 9', 'Advanced health monitoring, fitness tracking, and seamless integration with iPhone. GPS + Cellular options available.', 399.99, 75, 'https://via.placeholder.com/400x300/10b981/ffffff?text=Apple+Watch', NOW()),

('iPad Air (5th Gen)', '10.9-inch Liquid Retina display with M1 chip. Perfect balance of performance, capability, and portability.', 599.99, 30, 'https://via.placeholder.com/400x300/8b5cf6/ffffff?text=iPad+Air', NOW()),

('Logitech MX Master 3S', 'Advanced wireless mouse with ultra-fast scrolling, ergonomic design, and multi-device connectivity.', 99.99, 80, 'https://via.placeholder.com/400x300/f59e0b/ffffff?text=MX+Master+3S', NOW()),

('Mechanical Gaming Keyboard', 'RGB backlit mechanical keyboard with Cherry MX switches, programmable keys, and aluminum frame.', 159.99, 45, 'https://via.placeholder.com/400x300/ec4899/ffffff?text=Gaming+Keyboard', NOW()),

('Dell UltraSharp 27" 4K', 'Professional 4K monitor with 99% sRGB color accuracy, USB-C connectivity, and ergonomic stand.', 549.99, 25, 'https://via.placeholder.com/400x300/06b6d4/ffffff?text=Dell+Monitor', NOW()),

('Logitech C920 HD Webcam', 'Full HD 1080p webcam with auto-focus, built-in microphones, and universal compatibility.', 79.99, 60, 'https://via.placeholder.com/400x300/84cc16/ffffff?text=Webcam', NOW()),

('Bose SoundLink Flex', 'Portable Bluetooth speaker with waterproof design, 12-hour battery life, and premium sound quality.', 149.99, 40, 'https://via.placeholder.com/400x300/6366f1/ffffff?text=Bose+Speaker', NOW()),

('Samsung Galaxy Tab S9', 'Premium Android tablet with S Pen, 120Hz AMOLED display, and desktop-class performance.', 799.99, 20, 'https://via.placeholder.com/400x300/f97316/ffffff?text=Galaxy+Tab', NOW()),

('Sony WH-1000XM5', 'Industry-leading noise canceling wireless headphones with exceptional sound quality and 30-hour battery.', 399.99, 35, 'https://via.placeholder.com/400x300/0ea5e9/ffffff?text=Sony+Headphones', NOW()),

('Nintendo Switch OLED', 'Gaming console with 7-inch OLED screen, enhanced audio, and 64GB internal storage. Joy-Con controllers included.', 349.99, 50, 'https://via.placeholder.com/400x300/dc2626/ffffff?text=Nintendo+Switch', NOW()),

('Google Pixel Buds Pro', 'Wireless earbuds with Active Noise Cancellation, 11-hour battery life, and Google Assistant integration.', 199.99, 70, 'https://via.placeholder.com/400x300/059669/ffffff?text=Pixel+Buds', NOW()),

('Microsoft Surface Pro 9', '2-in-1 laptop and tablet with Intel 12th Gen processors, 13-inch touchscreen, and Type Cover included.', 1299.99, 18, 'https://via.placeholder.com/400x300/7c3aed/ffffff?text=Surface+Pro', NOW()),

('Canon EOS R5', 'Professional mirrorless camera with 45MP full-frame sensor, 8K video recording, and advanced autofocus.', 3899.99, 12, 'https://via.placeholder.com/400x300/991b1b/ffffff?text=Canon+R5', NOW()),

('Tesla Model Y Charger', 'Official Tesla home charging station with 48A power delivery and WiFi connectivity for remote monitoring.', 549.99, 25, 'https://via.placeholder.com/400x300/374151/ffffff?text=Tesla+Charger', NOW()),

('DJI Mini 4 Pro', 'Compact drone with 4K HDR video, omnidirectional obstacle sensing, and 34-minute flight time.', 759.99, 30, 'https://via.placeholder.com/400x300/1e40af/ffffff?text=DJI+Mini+4', NOW()),

('Razer Gaming Chair', 'Ergonomic gaming chair with lumbar support, 4D armrests, and premium PVC leather upholstery.', 399.99, 15, 'https://via.placeholder.com/400x300/16a34a/ffffff?text=Gaming+Chair', NOW()),

('SteelSeries Arctis 7P', 'Wireless gaming headset with 24-hour battery life, ClearCast microphone, and surround sound.', 149.99, 40, 'https://via.placeholder.com/400x300/ca8a04/ffffff?text=Gaming+Headset', NOW()),

('Samsung 980 PRO SSD', 'High-performance NVMe SSD with 1TB storage, PCIe 4.0 interface, and sequential read speeds up to 7,000 MB/s.', 129.99, 55, 'https://via.placeholder.com/400x300/dc2626/ffffff?text=Samsung+SSD', NOW()),

('ASUS ROG Strix RTX 4080', 'High-end graphics card with 16GB GDDR6X memory, advanced cooling, and RGB lighting for gaming enthusiasts.', 1199.99, 8, 'https://via.placeholder.com/400x300/7c2d12/ffffff?text=RTX+4080', NOW()),

('Herman Miller Aeron Chair', 'Iconic ergonomic office chair with breathable mesh, PostureFit SL support, and 12-year warranty.', 1395.99, 10, 'https://via.placeholder.com/400x300/065f46/ffffff?text=Aeron+Chair', NOW()),

('Sonos Arc Soundbar', 'Premium soundbar with Dolby Atmos, voice control, and seamless integration with Sonos ecosystem.', 899.99, 22, 'https://via.placeholder.com/400x300/1e3a8a/ffffff?text=Sonos+Arc', NOW()),

('Fitbit Versa 4', 'Advanced fitness smartwatch with GPS, heart rate monitoring, sleep tracking, and 6+ day battery life.', 199.99, 65, 'https://via.placeholder.com/400x300/be123c/ffffff?text=Fitbit+Versa', NOW());
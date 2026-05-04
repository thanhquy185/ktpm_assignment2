-- CATEGORIES
INSERT INTO categories(id, name)
VALUES  (gen_random_uuid(), 'Laptop'),
    (gen_random_uuid(), 'PC Gaming'),
    (gen_random_uuid(), 'Phụ kiện'),
    (gen_random_uuid(), 'Màn hình'),
    (gen_random_uuid(), 'Chuột & Bàn phím');

-- USERS
INSERT INTO users(id, role, username, password, refresh_token)
VALUES  (gen_random_uuid(), 'ADMIN', 'qchh', '$2a$10$iWmFzN.89701WHeAVazxa.jQAPgB8BGFgPzv5yIdTUhu0hspjnIgy', NULL),
    (gen_random_uuid(), 'CUSTOMER', 'customer', '$2a$10$3hpzTdkhR2dfQ0BsAedILeq4Ly84AibcV6G1697fnFYStKU4lK44K', NULL);

-- COUPONS
INSERT INTO coupons(id, code, date_start, date_end, type, discount)
VALUES (gen_random_uuid(), 'SALE10%', CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '10 days', 'PERCENT', 10),
    (gen_random_uuid(), 'SALE50K', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '5 days', 'FIXED', 50000),
    (gen_random_uuid(), 'FLASH20', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '1 day', 'PERCENT', 20),
    (gen_random_uuid(), 'OLD10', CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '1 day', 'PERCENT', 10),
    (gen_random_uuid(), 'OLD50K', CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '5 days', 'FIXED', 50000);

-- PRODUCTS
INSERT INTO products(id, name, description, price, status, category_id)
SELECT gen_random_uuid(), 'MacBook Pro M3', 'Laptop Apple mạnh mẽ', 45000000, 'ACTIVE', id FROM categories WHERE name = 'Laptop';
INSERT INTO products(id, name, description, price, status, category_id)
SELECT gen_random_uuid(), 'Dell XPS 13', 'Laptop cao cấp', 35000000, 'ACTIVE', id FROM categories WHERE name = 'Laptop';
INSERT INTO products(id, name, description, price, status, category_id)
SELECT gen_random_uuid(), 'PC RTX 4090', 'PC Gaming cực mạnh', 70000000, 'ACTIVE', id FROM categories WHERE name = 'PC Gaming';
INSERT INTO products(id, name, description, price, status, category_id)
SELECT gen_random_uuid(), 'Màn hình LG 27 inch', '4K UHD', 8000000, 'ACTIVE', id FROM categories WHERE name = 'Màn hình';
INSERT INTO products(id, name, description, price, status, category_id)
SELECT gen_random_uuid(), 'Chuột Logitech G Pro', 'Chuột gaming', 2500000, 'ACTIVE', id FROM categories WHERE name = 'Chuột & Bàn phím';

-- INVENTORY
INSERT INTO inventories(id, stock, product_id) 
SELECT gen_random_uuid(), 5, id FROM products;

-- CART (cho tất cả user)
INSERT INTO carts(id, total_quantity, total_price, user_id)
SELECT gen_random_uuid(), 0, 0, id FROM users LIMIT 2;

-- ORDER (cho customer)
INSERT INTO orders (
    id, created_at, shipping_address, shipping_method, payment_method,
    subtotal, discount, shipping_fee, total_price, status, user_id, coupon_id
)
VALUES (gen_random_uuid(), NOW(), '123 Nguyễn Văn A, TP.HCM', 'STANDARD',
        'COD', 45000000, 4500000, 30000, 40530000, 'PENDING',
        (SELECT id FROM users WHERE username = 'customer'),
        (SELECT id FROM coupons WHERE code = 'SALE10%')),
    (gen_random_uuid(), NOW(), '456 Lê Lợi, TP.HCM', 'EXPRESS',
        'BANK', 70000000, 50000, 50000, 69950000, 'CONFIRMED',
        (SELECT id FROM users WHERE username = 'customer'),
        (SELECT id FROM coupons WHERE code = 'SALE50K')),
    (gen_random_uuid(), NOW(), '789 Trần Hưng Đạo, TP.HCM', 'STANDARD',
        'COD', 10500000, 0, 30000, 10530000, 'DELIVERED',
        (SELECT id FROM users WHERE username = 'customer'),
        NULL);

-- ORDER ITEMS 
-- Đơn 1: MacBook Pro
INSERT INTO order_items(id, quantity, price, order_id, product_id)
SELECT gen_random_uuid(), 1, p.price, o.id, p.id
FROM orders o, products p
WHERE p.name = 'MacBook Pro M3'
ORDER BY o.created_at ASC
LIMIT 1;
-- Đơn 2: PC RTX 4090
INSERT INTO order_items(id, quantity, price, order_id, product_id)
SELECT gen_random_uuid(), 1, p.price, o.id, p.id
FROM orders o, products p
WHERE p.name = 'PC RTX 4090'
ORDER BY o.created_at ASC
OFFSET 1 LIMIT 1;
-- Đơn 3: Màn hình + chuột
INSERT INTO order_items(id, quantity, price, order_id, product_id)
SELECT gen_random_uuid(), 1, p.price, o.id, p.id
FROM orders o, products p
WHERE p.name = 'Màn hình LG 27 inch'
ORDER BY o.created_at ASC
OFFSET 2 LIMIT 1;
INSERT INTO order_items(id, quantity, price, order_id, product_id)
SELECT gen_random_uuid(), 1, p.price, o.id, p.id
FROM orders o, products p
WHERE p.name = 'Chuột Logitech G Pro'
ORDER BY o.created_at ASC
OFFSET 2 LIMIT 1;
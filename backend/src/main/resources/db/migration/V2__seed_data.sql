-- CATEGORIES
INSERT INTO categories(id, name)
VALUES  (gen_random_uuid(), 'Laptop'),
    (gen_random_uuid(), 'PC Gaming'),
    (gen_random_uuid(), 'Phụ kiện'),
    (gen_random_uuid(), 'Màn hình'),
    (gen_random_uuid(), 'Chuột & Bàn phím');

-- USERS
INSERT INTO users(id, role, username, password)
VALUES  (gen_random_uuid(), 'ADMIN', 'admin', '123456'),
    (gen_random_uuid(), 'CUSTOMER', 'user1', '123456');

-- COUPONS
INSERT INTO coupons(id, code, type, discount)
VALUES  (gen_random_uuid(), 'SALE10', 'PERCENT', 10),
    (gen_random_uuid(), 'SALE50K', 'FIXED', 50000);

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

-- CART (cho user)
INSERT INTO carts(id, total_quantity, total_price, user_id)
SELECT gen_random_uuid(), 0, 0, id FROM users LIMIT 1;
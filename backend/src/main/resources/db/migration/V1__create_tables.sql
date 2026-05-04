-- USERS
CREATE TABLE users (
    id UUID PRIMARY KEY,
    role VARCHAR(8) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    refresh_token TEXT
);

-- CATEGORIES
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- PRODUCTS
CREATE TABLE products (
    id UUID PRIMARY KEY,
    image VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    category_id UUID NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES categories(id)
);

-- INVENTORIES (1-1 product)
CREATE TABLE inventories (
    id UUID PRIMARY KEY,
    stock BIGINT NOT NULL,
    product_id UUID NOT NULL,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id)
        REFERENCES products(id)
);

-- CARTS (1-1 user)
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    total_quantity BIGINT NOT NULL,
    total_price BIGINT NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id)
        REFERENCES users(id)
);

-- CART ITEMS
CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    quantity BIGINT NOT NULL,
    price BIGINT NOT NULL,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id)
        REFERENCES carts(id),
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id)
        REFERENCES products(id)
);

-- COUPONS
CREATE TABLE coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    date_start DATE NOT NULL,
    date_end DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    discount BIGINT NOT NULL
);

-- ORDERS
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    shipping_method VARCHAR(8) NOT NULL,
    payment_method VARCHAR(4) NOT NULL,
    subtotal BIGINT NOT NULL,
    discount DOUBLE PRECISION NOT NULL,
    shipping_fee BIGINT NOT NULL,
    total_price DOUBLE PRECISION NOT NULL,
    status VARCHAR(9) NOT NULL,
    user_id UUID NOT NULL,
    coupon_id UUID NULl,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_order_coupon FOREIGN KEY (coupon_id)
        REFERENCES coupons(id)
);

-- ORDER ITEMS
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    quantity BIGINT NOT NULL,
    price BIGINT NOT NULL,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id)
        REFERENCES orders(id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id)
        REFERENCES products(id)
);

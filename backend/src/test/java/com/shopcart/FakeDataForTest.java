package com.shopcart;

import java.util.List;
import java.util.UUID;

import com.shopcart.entities.Cart;
import com.shopcart.entities.CartItem;
import com.shopcart.entities.Category;
import com.shopcart.entities.Coupon;
import com.shopcart.entities.Inventory;
import com.shopcart.entities.Order;
import com.shopcart.entities.OrderItem;
import com.shopcart.entities.Product;
import com.shopcart.entities.User;
import com.shopcart.enums.CouponTypeEnum;
import com.shopcart.enums.OrderStatusEnum;
import com.shopcart.enums.ProductStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeDataForTest {
        // User id
        private UUID userIdFake1;
        private UUID userIdFake2;
        // User
        private User userFake1;
        private User userFake2;
        // Category
        private Category categoryFake1;
        private Category categoryFake2;
        private Category categoryFake3;
        private Category categoryFake4;
        private Category categoryFake5;
        // Inventory
        private Inventory inventoryFake1;
        private Inventory inventoryFake2;
        private Inventory inventoryFake3;
        private Inventory inventoryFake4;
        private Inventory inventoryFake5;
        // Product id
        private UUID productIdFake1;
        private UUID productIdFake2;
        private UUID productIdFake3;
        private UUID productIdFake4;
        private UUID productIdFake5;
        // Product
        private Product productFake1;
        private Product productFake2;
        private Product productFake3;
        private Product productFake4;
        private Product productFake5;
        // Products
        private List<Product> productsFake;
        // Cart id
        private UUID cartIdFake1;
        private UUID cartIdFake2;
        // Cart
        private Cart cartFake1;
        private Cart cartFake2;
        // Cart item id
        private UUID cartItemIdFake1;
        // Cart item
        private CartItem cartItemFake1;
        // Carts
        private List<Cart> cartsFake;
        // Coupon
        private Coupon couponFake1;
        private Coupon couponFake2;
        // Order id
        private UUID orderIdFake1;
        private UUID orderIdFake2;
        // Order
        private Order orderFake1;
        private Order orderFake2;
        // Order item id
        private UUID orderItemIdFake1;
        // Order item
        private OrderItem orderItemFake1;
        // Orders
        private List<Order> ordersFake;

        public FakeDataForTest() {
                this.fakeUserIdData();
                this.fakeUserData();
                this.fakeUserData();
                this.fakeCategoryData();
                this.fakeProductIdData();
                this.fakeProductData();
                this.fakeInventoryData();
                this.fakeProductsData();
                this.fakeCartIdData();
                this.fakeCartData();
                this.fakeCartItemIdData();
                this.fakeCartItemData();
                this.fakeCartsData();
                this.fakeCouponData();
                this.fakeOrderIdData();
                this.fakeOrderData();
                this.fakeOrderItemIdData();
                this.fakeOrderItemData();
                this.fakeOrdersData();
        }

        private void fakeUserIdData() {
                this.userIdFake1 = UUID.randomUUID();

                this.userIdFake2 = UUID.randomUUID();
        }

        private void fakeUserData() {
                this.userFake1 = User.builder()
                                .id(userIdFake1)
                                .role("ADMIN")
                                .username("admin")
                                .password("admin")
                                .build();

                this.userFake2 = User.builder()
                                .id(userIdFake2)
                                .role("CUSTOMER")
                                .username("customer")
                                .password("customer")
                                .build();
        }

        private void fakeCategoryData() {
                this.categoryFake1 = Category.builder()
                                .id(UUID.randomUUID())
                                .name("Laptop Window")
                                .build();

                this.categoryFake2 = Category.builder()
                                .id(UUID.randomUUID())
                                .name("Laptop Apple")
                                .build();

                this.categoryFake3 = Category.builder()
                                .id(UUID.randomUUID())
                                .name("Iphone")
                                .build();

                this.categoryFake4 = Category.builder()
                                .id(UUID.randomUUID())
                                .name("Samsung")
                                .build();

                this.categoryFake5 = Category.builder()
                                .id(UUID.randomUUID())
                                .name("Mouse")
                                .build();
        }

        private void fakeProductIdData() {
                this.productIdFake1 = UUID.randomUUID();

                this.productIdFake2 = UUID.randomUUID();

                this.productIdFake3 = UUID.randomUUID();

                this.productIdFake4 = UUID.randomUUID();

                this.productIdFake5 = UUID.randomUUID();
        }

        private void fakeProductData() {
                this.productFake1 = Product.builder()
                                .id(this.productIdFake1)
                                .image(null)
                                .name("Dell XPS 13")
                                .price(30000000L)
                                .description(null)
                                .status(ProductStatusEnum.ACTIVE)
                                .category(this.categoryFake1)
                                .build();

                this.productFake2 = Product.builder()
                                .id(this.productIdFake2)
                                .image(null)
                                .name("MacBook Pro M3")
                                .price(45000000L)
                                .description(null)
                                .status(ProductStatusEnum.ACTIVE)
                                .category(this.categoryFake2)
                                .build();

                this.productFake3 = Product.builder()
                                .id(this.productIdFake3)
                                .image(null)
                                .name("iPhone 15 Pro")
                                .price(28000000L)
                                .description(null)
                                .status(ProductStatusEnum.INACTIVE)
                                .category(this.categoryFake3)
                                .build();

                this.productFake4 = Product.builder()
                                .id(this.productIdFake4)
                                .image(null)
                                .name("Samsung S24")
                                .price(25000000L)
                                .description(null)
                                .status(ProductStatusEnum.ACTIVE)
                                .category(this.categoryFake4)
                                .build();

                this.productFake5 = Product.builder()
                                .id(this.productIdFake5)
                                .image(null)
                                .name("Logitech MX Master 3")
                                .price(2500000L)
                                .description(null)
                                .status(ProductStatusEnum.INACTIVE)
                                .category(this.categoryFake5)
                                .build();
        }

        private void fakeInventoryData() {
                this.inventoryFake1 = Inventory.builder()
                                .id(UUID.randomUUID())
                                .stock(5L)
                                .product(null)
                                .build();
                this.productFake1.setInventory(this.inventoryFake1);

                this.inventoryFake2 = Inventory.builder()
                                .id(UUID.randomUUID())
                                .stock(5L)
                                .product(null)
                                .build();
                this.productFake2.setInventory(this.inventoryFake2);

                this.inventoryFake3 = Inventory.builder()
                                .id(UUID.randomUUID())
                                .stock(5L)
                                .product(null)
                                .build();
                this.productFake3.setInventory(this.inventoryFake3);

                this.inventoryFake4 = Inventory.builder()
                                .id(UUID.randomUUID())
                                .stock(5L)
                                .product(null)
                                .build();
                this.productFake4.setInventory(this.inventoryFake4);

                this.inventoryFake5 = Inventory.builder()
                                .id(UUID.randomUUID())
                                .stock(5L)
                                .product(null)
                                .build();
                this.productFake5.setInventory(this.inventoryFake5);
        }

        private void fakeProductsData() {
                this.productsFake = List.of(this.productFake1,
                                this.productFake2,
                                this.productFake3,
                                this.productFake4,
                                this.productFake5);
        }

        private void fakeCartIdData() {
                this.cartIdFake1 = UUID.randomUUID();

                this.cartIdFake2 = UUID.randomUUID();
        }

        private void fakeCartData() {
                this.cartFake1 = Cart.builder()
                                .id(this.cartIdFake1)
                                .totalQuantity(0L)
                                .totalPrice(0L)
                                .cartItems(List.<CartItem>of())
                                .user(User.builder().id(this.userFake1.getId()).build())
                                .build();
                this.userFake1.setCart(this.cartFake1);

                this.cartFake2 = Cart.builder()
                                .id(this.cartIdFake2)
                                .totalQuantity(0L)
                                .totalPrice(0L)
                                .cartItems(List.<CartItem>of())
                                .user(User.builder().id(this.userFake2.getId()).build())
                                .build();
                this.userFake2.setCart(this.cartFake2);

        }

        private void fakeCartItemIdData() {
                this.cartItemIdFake1 = UUID.randomUUID();
        }

        private void fakeCartItemData() {
                this.cartItemFake1 = CartItem.builder()
                                .id(this.cartIdFake1)
                                .price(this.productFake1.getPrice())
                                .quantity(3L)
                                .cart(null)
                                .product(this.productFake1)
                                .build();
                this.cartFake1.setCartItems(List.<CartItem>of(this.cartItemFake1));
                this.cartFake1.setTotalPrice(cartItemFake1.getQuantity() * cartItemFake1.getPrice());
                this.cartFake1.setTotalQuantity(cartItemFake1.getQuantity());
        }

        private void fakeCartsData() {
                this.cartsFake = List.<Cart>of(this.cartFake1, this.cartFake2);
        }

        private void fakeCouponData() {
                this.couponFake1 = Coupon.builder()
                                .id(UUID.randomUUID())
                                .code("SALE10%")
                                .type(CouponTypeEnum.PERCENT)
                                .discount(10L)
                                .build();

                this.couponFake2 = Coupon.builder()
                                .id(UUID.randomUUID())
                                .code("SALE10K")
                                .type(CouponTypeEnum.FIXED)
                                .discount(10000L)
                                .build();
        }

        private void fakeOrderIdData() {
                this.orderIdFake1 = UUID.randomUUID();

                this.orderIdFake2 = UUID.randomUUID();
        }

        private void fakeOrderData() {
                this.orderFake1 = Order.builder()
                                .id(this.orderIdFake1)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .subtotal(60000000L)
                                .shippingFee(10000L)
                                .discount(6000000D)
                                .totalPrice(54010000D)
                                .status(OrderStatusEnum.PENDING)
                                .orderItems(List.<OrderItem>of())
                                .user(null)
                                .coupon(this.couponFake1)
                                .build();
                this.userFake1.setOrders(List.<Order>of(this.orderFake1));

                this.orderFake2 = Order.builder()
                                .id(this.orderIdFake2)
                                .shippingAddress(null)
                                .subtotal(null)
                                .shippingFee(null)
                                .discount(null)
                                .totalPrice(null)
                                .status(OrderStatusEnum.PENDING)
                                .orderItems(List.<OrderItem>of())
                                .user(null)
                                .coupon(null)
                                .build();
                this.userFake2.setOrders(List.<Order>of(this.orderFake2));

        }

        private void fakeOrderItemIdData() {
                this.orderItemIdFake1 = UUID.randomUUID();
        }

        private void fakeOrderItemData() {
                this.orderItemFake1 = OrderItem.builder()
                                .id(this.orderIdFake1)
                                .price(this.productFake1.getPrice())
                                .quantity(3L)
                                .order(null)
                                .product(this.productFake1)
                                .build();
                this.orderFake1.setOrderItems(List.of(this.orderItemFake1));
        }

        private void fakeOrdersData() {
                this.ordersFake = List.<Order>of(this.orderFake1, this.orderFake2);
        }
}

import { test } from "@playwright/test";
import { CartPage } from "./page-object/CartPage";
import { CartType } from "../../src/types/cart";
import { CartItemType } from "../../src/types/cartItem";
import { LoginPage } from "./page-object/LoginPage";
import { ProductPage } from "./page-object/ProductPage";

// const productsFixture: ProductType[] = [
//   {
//     id: "PROD-001",
//     image: "macbook.png",
//     name: "Màn hình ASUS 24 inch",
//     category: { name: "Màn hình" },
//     inventory: { stock: 10 },
//     price: 2500000,
//     description: "Màn hình 24 inch chất lượng cao",
//     status: "Đang bán",
//   },
//   {
//     id: "PROD-002",
//     name: "Macbook M4 Air",
//     category: { name: "Laptop" },
//     inventory: { stock: 5 },
//     price: 5000000,
//     description: "Macbook M4 Air mạnh mẽ",
//     status: "Đang bán",
//   },
//   {
//     id: "PROD-003",
//     name: "Chuột Gaming",
//     category: { name: "Chuột" },
//     inventory: { stock: 2 },
//     price: 200000,
//     description: "Chuột gaming chính xác",
//     status: "Đang bán",
//   },
//   {
//     id: "PROD-004",
//     name: "Bàn phím Cơ",
//     category: { name: "Bàn phím" },
//     inventory: { stock: 0 },
//     price: 1500000,
//     description: "Bàn phím cơ cao cấp",
//     status: "Dừng bán",
//   },
// ];

const product1FakeData = {
  id: "PROD-001",
  name: "Macbook M4 Air",
  price: 30000000,
  status: "Đang bán",
  category: { name: "Macbook" },
  inventory: { stock: 10 },
  image: "macbook.png",
  description: "Test product",
};
const product2FakeData = {
  id: "PROD-002",
  name: "Laptop DELL",
  price: 20000000,
  status: "Đang bán",
  category: { name: "Laptop" },
  inventory: { stock: 1 },
  image: "laptop-dell.png",
  description: "Test product",
};

test.describe("Cart E2E Tests", () => {
  let cartItems: CartItemType[] = [
    { id: "CART-002", product: product2FakeData, quantity: 1 },
  ];

  test.beforeEach(async ({ page }) => {
    await page.route("**/api/users/login", async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          token: "fake-token",
        }),
      });
    });
    await page.route("**/api/users/info", async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "USER-001",
          username: "customer",
          role: "CUSTOMER",
        }),
      });
    });
    await page.route("**/api/products", (route) =>
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([product1FakeData, product2FakeData]),
      }),
    );
    await page.route("**/api/carts/user/USER-001", async (route) => {
      const method = route.request().method();

      if (method === "GET") {
        const totalQuantity = cartItems?.reduce(
          (total, cartItem) => total + (cartItem.quantity ?? 0),
          0,
        );
        const totalPrice = cartItems?.reduce(
          (total, cartItem) =>
            total + (cartItem.quantity ?? 0) * (cartItem.product?.price ?? 0),
          0,
        );

        await route.fulfill({
          status: 200,
          contentType: "application/json",
          body: JSON.stringify({
            status: 200,
            error: null,
            message: "Get cart by user id is successful!",
            data: {
              totalQuantity: totalQuantity,
              totalPrice: totalPrice,
              cartItems: cartItems,
            } as CartType,
          }),
        });

        return;
      }
      if (method === "POST") {
        const requestBody = await route.request().postDataJSON();
        const productId = requestBody.productId;
        const product =
          productId === product1FakeData.id
            ? product1FakeData
            : product2FakeData;

        const existingItem = cartItems.find(
          (item) => item.product?.id === product.id,
        );
        const currentQuantity = existingItem?.quantity ?? 0;
        const stock = product.inventory?.stock ?? 0;
        if (currentQuantity + 1 > stock) {
          await route.fulfill({
            status: 400,
            contentType: "application/json",
            body: JSON.stringify({
              status: 400,
              error: "INSUFFICIENT_STOCK",
              message: "Insufficient stock for product ID PROD-002",
              data: null,
            }),
          });

          return;
        }
        if (existingItem) {
          existingItem.quantity! += 1;

          await route.fulfill({
            status: 201,
            contentType: "application/json",
            body: JSON.stringify({
              status: 201,
              error: null,
              message: "Add to cart is successful!",
              data: existingItem,
            }),
          });

          return;
        }

        const newItem: CartItemType = {
          id: "CART-001",
          product,
          quantity: 1,
        };
        cartItems.push(newItem);

        await route.fulfill({
          status: 201,
          contentType: "application/json",
          body: JSON.stringify({
            status: 201,
            error: null,
            message: "Add to cart is successful!",
            data: newItem,
          }),
        });

        return;
      }

      await route.continue();
    });

    const loginPage = new LoginPage(page);
    await loginPage.open();
    await loginPage.login("customer", "customer");
  });

  test("Thêm sản phẩm vào giỏ hàng thành công", async ({ page }) => {
    const productPage = new ProductPage(page);
    const cartPage = new CartPage(page);

    await productPage.open();

    await productPage.clickFirstProductButton();

    await productPage.showToastAddToCartSuccess();

    await cartPage.open();

    await cartPage.hiddenEmptyCartInform();

    await cartPage.checkCartItem("CART-001", "Macbook M4 Air");

    await cartPage.checkTotalQuantity(2);

    await cartPage.checkTotalPrice(50000000);
  });

  test("Thêm sản phẩm vào giỏ hàng nhưng tồn kho sản phẩm không đủ", async ({
    page,
  }) => {
    const productPage = new ProductPage(page);
    const cartPage = new CartPage(page);

    await productPage.open();

    await productPage.clickSecondProductButton();

    await productPage.showToastAddToCartError();

    await cartPage.open();

    await cartPage.hiddenEmptyCartInform();

    await cartPage.checkCartItem("CART-002", "Laptop DELL");
  });
});

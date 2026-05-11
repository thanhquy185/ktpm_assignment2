import { errors, expect, test } from "@playwright/test";
import { CartPage } from "./page-object/CartPage";
import { CartItemType } from "../../src/types/cartItem";
import { ProductType } from "../../src/types/product";
import { LoginPage } from "./page-object/LoginPage";
import { ProductPage } from "./page-object/ProductPage";
import { InventoryType } from "../../src/types/inventory";

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

const productFakeData = {
  id: "PROD-001",
  name: "Macbook M4 Air",
  price: 30000000,
  status: "Đang bán",
  category: { name: "Laptop" },
  inventory: { stock: 10 },
  image: "macbook.png",
  description: "Test product",
};

test.describe("Cart E2E Tests", () => {
  let cartItems: CartItemType[] = [];

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
        body: JSON.stringify([productFakeData]),
      }),
    );
    await page.route("**/api/carts/user/USER-001", async (route) => {
      const method = route.request().method();

      if (method === "GET") {
        await route.fulfill({
          status: 200,
          contentType: "application/json",
          body: JSON.stringify({
            status: 200,
            error: null,
            message: "Get cart by user id is successful!",
            data: cartItems,
          }),
        });

        return;
      }
      if (method === "POST") {
        const product = productFakeData;

        const existingItem = cartItems.find(
          (item) => item.product?.id === product.id,
        );
        if (existingItem) {
          existingItem.quantity! += 1;

          await route.fulfill({
            status: 201,
            contentType: "application/json",
            body: JSON.stringify({
              status: 201,
              error: null,
              message: "Increase quantity is successful!",
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

    // await page.waitForResponse(
    //   (res) =>
    //     res.url().includes("/api/carts/user/USER-001") &&
    //     res.request().method() === "POST",
    // );
    // await page.waitForResponse((res) =>
    //   res.url().includes("/api/carts/products"),
    // );

    await productPage.showToastAddToCartSuccess();

    // await page.waitForResponse(
    //   (res) =>
    //     res.url().includes("/api/carts/user/USER-001") &&
    //     res.request().method() === "GET",
    // );

    // page.on("request", (req) => {
    //   console.log("REQ:", req.method(), req.url());
    // });
    // page.on("response", (res) => {
    //   console.log("RES:", res.url(), res.status());
    // });

    await cartPage.open();

    await cartPage.hiddenEmptyCartInform();

    // await cartPage.checkFirstCartItem("Macbook M4 Air");
  });
});

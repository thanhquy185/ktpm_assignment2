import { errors, expect, test } from "@playwright/test";
import { CartPage } from "./page-object/CartPage";
import { CartItemType } from "../../src/types/cartItem";
import { ProductType } from "../../src/types/product";
import { LoginPage } from "./page-object/LoginPage";

const productsFixture: ProductType[] = [
  {
    id: "PROD-001",
    image: "macbook.png",
    name: "Màn hình ASUS 24 inch",
    category: { name: "Màn hình" },
    inventory: { stock: 10 },
    price: 2500000,
    description: "Màn hình 24 inch chất lượng cao",
    status: "Đang bán",
  },
  {
    id: "PROD-002",
    name: "Macbook M4 Air",
    category: { name: "Laptop" },
    inventory: { stock: 5 },
    price: 5000000,
    description: "Macbook M4 Air mạnh mẽ",
    status: "Đang bán",
  },
  {
    id: "PROD-003",
    name: "Chuột Gaming",
    category: { name: "Chuột" },
    inventory: { stock: 2 },
    price: 200000,
    description: "Chuột gaming chính xác",
    status: "Đang bán",
  },
  {
    id: "PROD-004",
    name: "Bàn phím Cơ",
    category: { name: "Bàn phím" },
    inventory: { stock: 0 },
    price: 1500000,
    description: "Bàn phím cơ cao cấp",
    status: "Dừng bán",
  },
];

// test.describe("Cart E2E flow", () => {
//   test.beforeEach(async ({ page }) => {
//     const cartItems: CartItemType[] = [];

//     await page.route("**/api/user/login", async (route) => {
//       await route.fulfill({
//         status: 200,
//         contentType: "application/json",
//         body: JSON.stringify({
//           accessToken: "fake-token",
//           user: {
//             id: "user-123",
//             username: "customer",
//           },
//         }),
//       });
//     });

//     // await page.route("**/api/products", async (route) => {
//     //   await route.fulfill({
//     //     status: 200,
//     //     contentType: "application/json",
//     //     body: JSON.stringify(productsFixture),
//     //   });
//     // });

//     // await page.route("**/api/cart**", async (route) => {
//     //   const url = route.request().url();
//     //   const method = route.request().method();

//     //   // GET /api/cart/:userId
//     //   if (method === "GET") {
//     //     await route.fulfill({
//     //       status: 200,
//     //       contentType: "application/json",
//     //       body: JSON.stringify({
//     //         id: "cart-123",
//     //         userId: "user-123",
//     //         cartItems: cartItems,
//     //         totalPrice: cartItems.reduce(
//     //           (sum, item) =>
//     //             sum + (item.product?.price || 0) * (item.quantity || 0),
//     //           0,
//     //         ),
//     //         totalQuantity: cartItems.reduce(
//     //           (sum, item) => sum + (item.quantity || 0),
//     //           0,
//     //         ),
//     //       }),
//     //     });
//     //   }
//     //   // POST /api/cart/add (add to cart)
//     //   else if (method === "POST" && url.includes("/add")) {
//     //     const req = route.request();
//     //     let body: any = {};
//     //     try {
//     //       body = JSON.parse(req.postData() || "{}");
//     //     } catch (e) {
//     //       body = {};
//     //     }

//     //     const productId = body.productId || "";
//     //     const quantity = Number(body.quantity || 1);

//     //     const prod = productsFixture.find((p) => p.id === productId);
//     //     if (!prod) {
//     //       await route.fulfill({
//     //         status: 400,
//     //         contentType: "application/json",
//     //         body: JSON.stringify({
//     //           error: "Product not found",
//     //           message: "Sản phẩm không tồn tại",
//     //         }),
//     //       });
//     //       return;
//     //     }

//     //     const existing = cartItems.find((ci) => ci.product?.id === productId);
//     //     if (existing) {
//     //       existing.quantity = Math.min(
//     //         (existing.quantity || 0) + quantity,
//     //         prod.inventory?.stock || 0,
//     //       );
//     //     } else {
//     //       cartItems.push({
//     //         id: `CART-${Date.now()}`,
//     //         product: prod,
//     //         quantity: quantity,
//     //       });
//     //     }

//     //     await route.fulfill({
//     //       status: 201,
//     //       contentType: "application/json",
//     //       body: JSON.stringify({
//     //         data: { success: true },
//     //         message: "Đã thêm vào giỏ",
//     //       }),
//     //     });
//     //   }
//     // });

//     const loginPage = new LoginPage(page);
//     await loginPage.open();
//     await loginPage.loginAsCustomer();
//   });

//   test("TC1: Thêm sản phẩm vào giỏ hàng", async ({ page }) => {
//     const cartPage = new CartPage(page);
//     await cartPage.openShop();

//     // // Open shop and verify products are visible
//     // await expect(page.getByTestId("products-title")).toBeVisible();

//     // // Get initial cart count
//     // const oldCount = await cartPage.getCartBadgeCount();

//     // // Add first product
//     // await cartPage.addProductByName("Màn hình ASUS 24 inch");
//     // await cartPage.expectAddSuccessToast();

//     // // Verify cart badge increased
//     // const newCount = await cartPage.getCartBadgeCount();
//     // expect(newCount).toBeGreaterThan(oldCount);

//     // // Open cart and verify product is there
//     // await cartPage.openCart();
//     // await expect(
//     //   page.getByRole("button", { name: "Thanh toán ngay" }),
//     // ).toBeVisible();
//     // await expect(page.getByText("Màn hình ASUS 24 inch")).toBeVisible();
//   });
// });

test.describe("Cart E2E Tests", () => {
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
          id: 1,
          username: "customer",
          role: "CUSTOMER",
        }),
      });
    });
    await page.route("**/api/products*", (route) =>
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: 1,
            name: "Macbook M4 Air",
            price: 30000000,
            status: "Đang bán",
            category: { name: "Laptop" },
            inventory: { stock: 10 },
            image: "macbook.png",
            description: "Test product",
          },
        ]),
      }),
    );
    await page.route("**/api/cart/**", async (route) => {
      await route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          status: 201,
          error: null,
          message: "Add to cart is successful!",
          data: {
            id: "1",
            product: {
              id: 1,
              name: "Macbook M4 Air",
              price: 30000000,
              status: "Đang bán",
              category: { name: "Laptop" },
              inventory: { stock: 10 },
              image: "macbook.png",
              description: "Test product",
            },
            quantity: 1,
          },
        }),
      });
    });

    await page.goto("http://localhost:5173/login");

    await page.fill('[data-testid="login-username-input"]', "customer");
    await page.fill('[data-testid="login-password-input"]', "customer");
    await page.click('[data-testid="login-button"]');

    await page.waitForTimeout(1500);

    await page.waitForURL("**/products");
  });

  test("Thêm sản phẩm vào giỏ hàng thành công", async ({ page }) => {
    await page.waitForLoadState("networkidle");

    const firstProductButton = page.getByTestId("product-card-button-1");
    await expect(firstProductButton).toBeVisible();
    await firstProductButton.click();

    // const toast = page.getByTestId("toast-add-to-cart-success");
    // await expect(toast).toBeVisible();

    const cartEmptyInform = page.locator('[data-testid="cart-empty-inform"]');
    await expect(cartEmptyInform).toBeHidden();
  });
});

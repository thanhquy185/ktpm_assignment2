import { expect, type Locator, type Page } from "@playwright/test";

export class CartPage {
  readonly page: Page;
  readonly cartBadge: Locator;
  readonly addToCartButtons: Locator;
  readonly cartHeading: Locator;

  constructor(page: Page) {
    this.page = page;
    this.cartBadge = page.getByLabel("Giỏ hàng").locator("span").last();
    this.addToCartButtons = page.getByRole("button", {
      name: "Thêm vào giỏ",
    });
    this.cartHeading = page.getByRole("heading", {
      name: "Giỏ hàng",
    });
  }

  async openShop(): Promise<void> {
    await this.page.goto("/products");
    await this.page.waitForLoadState("load");
    await expect(this.page.getByTestId("products-title")).toBeVisible();
  }

  async openCart(): Promise<void> {
    await this.page.getByLabel("Giỏ hàng").click();
    await expect(this.cartHeading).toBeVisible();
  }

  async getCartBadgeCount(): Promise<number> {
    const text = (await this.cartBadge.textContent()) ?? "0";
    return Number.parseInt(text.trim(), 10);
  }

  async addFirstAvailableProduct(): Promise<void> {
    await this.addToCartButtons.first().click();
  }

  async addProductByName(productName: string): Promise<void> {
    const card = this.page.locator("article", {
      has: this.page.getByRole("heading", {
        name: productName,
      }),
    });

    await card
      .getByRole("button", {
        name: "Thêm vào giỏ",
      })
      .click();
  }

  async expectAddSuccessToast(): Promise<void> {
    await expect(this.page.getByText("Đã thêm vào giỏ").first()).toBeVisible();
    // Wait for toast to disappear
    await this.page.waitForTimeout(2000);
  }

  async getQuantityForProduct(productName: string): Promise<number> {
    const row = this.page.locator(
      "div.bg-white.rounded-lg.shadow.p-4.flex.gap-4",
      {
        has: this.page.getByText(productName),
      },
    );
    const quantityCell = row.locator('[data-testId*="cart-item-quantity"]');
    const text = (await quantityCell.textContent()) ?? "0";

    return Number.parseInt(text.trim(), 10);
  }

  async increaseQuantityForProduct(productName: string): Promise<void> {
    const row = this.page.locator(
      "div.bg-white.rounded-lg.shadow.p-4.flex.gap-4",
      {
        has: this.page.getByText(productName),
      },
    );

    const increaseBtn = row.locator('[data-testId*="increase-quantity"]');
    await increaseBtn.click();
  }

  async decreaseQuantityForProduct(productName: string): Promise<void> {
    const row = this.page.locator(
      "div.bg-white.rounded-lg.shadow.p-4.flex.gap-4",
      {
        has: this.page.getByText(productName),
      },
    );

    const decreaseBtn = row.locator('[data-testId*="decrease-quantity"]');
    await decreaseBtn.click();
  }

  async expectIncreaseDisabledForProduct(productName: string): Promise<void> {
    const row = this.page.locator(
      "div.bg-white.rounded-lg.shadow.p-4.flex.gap-4",
      {
        has: this.page.getByText(productName),
      },
    );

    const increaseBtn = row.locator('[data-testId*="increase-quantity"]');
    await expect(increaseBtn).toBeDisabled();
  }

  async expectStockWarning(productName: string, stock: number): Promise<void> {
    const row = this.page.locator(
      "div.bg-white.rounded-lg.shadow.p-4.flex.gap-4",
      {
        has: this.page.getByText(productName),
      },
    );

    await expect(
      row.getByText(`Chỉ còn ${stock} sản phẩm trong kho!`),
    ).toBeVisible();
  }

  async removeProduct(productName: string): Promise<void> {
    const row = this.page.locator(
      "div.bg-white.rounded-lg.shadow.p-4.flex.gap-4",
      {
        has: this.page.getByText(productName),
      },
    );

    const removeBtn = row.locator('[data-testId*="remove-button"]');
    await removeBtn.click();
  }

  async goToCheckout(): Promise<void> {
    const checkoutBtn = this.page.getByRole("button", {
      name: "Thanh toán ngay",
    });
    await checkoutBtn.click();
    await this.page.waitForURL("**/checkout");
  }
}

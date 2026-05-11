import { expect, type Locator, type Page } from "@playwright/test";

export class ProductPage {
  readonly page: Page;
  readonly title: Locator;
  readonly firstProductButton: Locator;
  readonly toastAddToCartSuccess: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.getByTestId("products-title");
    this.firstProductButton = page.getByTestId("product-card-button-PROD-001");
    this.toastAddToCartSuccess = page.getByTestId("toast-add-to-cart-success");
  }

  async open(): Promise<void> {
    await this.page.waitForURL("**/products");
    await expect(this.title).toBeVisible();
    await expect(this.title).toContainText("Sản phẩm");
  }

  async clickFirstProductButton(): Promise<void> {
    await expect(this.firstProductButton).toBeVisible();
    await this.firstProductButton.click();
  }

  async showToastAddToCartSuccess(): Promise<void> {
    await expect(this.toastAddToCartSuccess).toBeVisible();
    await expect(this.toastAddToCartSuccess).toContainText(
      "Thêm sản phẩm vào giỏ hàng thành công!",
    );
  }
}

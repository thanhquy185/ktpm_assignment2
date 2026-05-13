import { expect, type Locator, type Page } from "@playwright/test";

export class ProductPage {
  readonly page: Page;
  readonly title: Locator;
  readonly firstProductButton: Locator;
  readonly secondProductButton: Locator;
  readonly toastAddToCartSuccess: Locator;
  readonly toastAddToCartError: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.getByTestId("products-title");
    this.firstProductButton = page.getByTestId("product-card-button-PROD-001");
    this.secondProductButton = page.getByTestId("product-card-button-PROD-002");
    this.toastAddToCartSuccess = page.locator(".Toastify__toast--success");
    this.toastAddToCartError = page.locator(".Toastify__toast--error");
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

  async clickSecondProductButton(): Promise<void> {
    await expect(this.secondProductButton).toBeVisible();
    await this.secondProductButton.click();
  }

  async showToastAddToCartSuccess(): Promise<void> {
    // await expect(this.toastAddToCartSuccess).toBeVisible();
    // await expect(this.toastAddToCartSuccess).toContainText(
    //   "Thêm sản phẩm vào giỏ hàng thành công!",
    // );
    await expect(
      this.page.getByText("Thêm sản phẩm vào giỏ hàng thành công!"),
    ).toBeVisible();
  }

  async showToastAddToCartError(): Promise<void> {
    // await expect(this.toastAddToCartError).toBeVisible();
    // await expect(this.toastAddToCartError).toContainText(
    //   "Insufficient stock for product ID PROD-002",
    // );
    await expect(
      this.page.getByText("Insufficient stock for product ID PROD-002"),
    ).toBeVisible();
  }
}

import { expect, type Locator, type Page } from "@playwright/test";

export class CartPage {
  readonly page: Page;
  readonly title: Locator;
  readonly goToCart: Locator;
  readonly cartEmptyInform: Locator;
  readonly cartFirstCartItemProductName: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.getByTestId("cart-title");
    this.goToCart = page.getByTestId("nav-cart");
    this.cartEmptyInform = page.getByTestId("cart-empty-inform");
    this.cartFirstCartItemProductName = page.getByTestId(
      "cart-item-product-name-CART-001",
    );
  }

  async open(): Promise<void> {
    await this.goToCart.click();
    await this.page.waitForURL("**/cart");
    await expect(this.title).toBeVisible();
    await expect(this.title).toContainText("Giỏ hàng");
  }

  async hiddenEmptyCartInform(): Promise<void> {
    await expect(this.cartEmptyInform).toBeHidden();
  }

  async checkFirstCartItem(productName: string): Promise<void> {
    await expect(this.cartFirstCartItemProductName).toContainText(productName);
  }
}

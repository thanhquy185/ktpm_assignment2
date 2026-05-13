import { expect, type Locator, type Page } from "@playwright/test";
import { formatPrice } from "../../../src/utils/priceCalculation";
export class CartPage {
  readonly page: Page;
  readonly title: Locator;
  readonly goToCart: Locator;
  readonly cartEmptyInform: Locator;
  readonly cartFirstCartItemProductName: Locator;
  readonly cartSecondCartItemProductName: Locator;
  readonly cartSummaryTotalQuantity: Locator;
  readonly cartSummaryTotalPrice: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.getByTestId("cart-title");
    this.goToCart = page.getByTestId("nav-cart");
    this.cartEmptyInform = page.getByTestId("cart-empty-inform");
    this.cartFirstCartItemProductName = page.getByTestId(
      "cart-item-product-name-CART-001",
    );
    this.cartSecondCartItemProductName = page.getByTestId(
      "cart-item-product-name-CART-002",
    );
    this.cartSummaryTotalQuantity = page.getByTestId(
      "cart-summary-total-quantity",
    );
    this.cartSummaryTotalPrice = page.getByTestId("cart-summary-total-price");
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

  async checkCartItem(cartItem: string, productName: string): Promise<void> {
    const cartItemCheck = this.page.getByTestId(
      `cart-item-product-name-${cartItem}`,
    );
    await expect(cartItemCheck).toContainText(productName);
  }

  async checkTotalQuantity(totalQuantity: number): Promise<void> {
    await expect(this.cartSummaryTotalQuantity).toContainText(
      totalQuantity + " sản phẩm",
    );
  }

  async checkTotalPrice(totalPrice: number): Promise<void> {
    await expect(this.cartSummaryTotalPrice).toContainText(
      formatPrice(totalPrice),
    );
  }
}

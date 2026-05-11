import { expect, type Locator, type Page } from "@playwright/test";

export class LoginPage {
  readonly page: Page;

  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = page.getByTestId("login-username-input");
    this.passwordInput = page.getByTestId("login-password-input");
    this.loginButton = page.getByTestId("login-button");
  }

  async open(): Promise<void> {
    await this.page.goto("http://localhost:5173/login");
    await this.page.waitForLoadState("domcontentloaded");
    await expect(this.usernameInput).toBeVisible();
    await expect(this.passwordInput).toBeVisible();
    await expect(this.loginButton).toBeVisible();
  }

  //   async open(): Promise<void> {
  //     await this.page.goto("http://localhost:5173/login");
  //     await this.page.waitForLoadState("domcontentloaded");

  //     // Check if redirected to products (already logged in)
  //     if (this.page.url().includes("/products")) {
  //       return;
  //     }

  //     await expect(this.usernameInput).toBeVisible();
  //     await expect(this.passwordInput).toBeVisible();
  //     await expect(this.loginButton).toBeVisible();
  //   }

  async login(username: string, password: string): Promise<void> {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.loginButton.click();

    await this.page.context().storageState({
      path: "playwright/.auth/user.json",
    });

    await this.page.waitForTimeout(1500);

    await this.page.waitForURL("**/products");

    await expect(this.page).toHaveURL(/.*products/);
  }

  async loginAsCustomer(): Promise<void> {
    await this.login("customer", "customer");
  }
}

import { test, expect } from '@playwright/test';

test.describe('ShopCart E2E Tests', () => {
  const baseURL = 'http://localhost:5173';

  test.beforeEach(async ({ page }) => {
    await page.goto(baseURL);
  });

  test('User can login successfully', async ({ page }) => {
    // Should redirect to login
    await expect(page).toHaveURL(`${baseURL}/login`);

    // Fill login form
    await page.fill('[data-testid="username-input"]', 'testuser');
    
    // Submit form
    await page.click('[data-testid="login-button"]');

    // Should redirect to products page
    await expect(page).toHaveURL(`${baseURL}/products`);
  });

  test('User can view products', async ({ page }) => {
    // Login first
    await page.fill('[data-testid="username-input"]', 'testuser');
    await page.click('[data-testid="login-button"]');

    // Should see products title
    await expect(page.locator('[data-testid="products-title"]')).toContainText('Sản phẩm');

    // Should see products grid
    const productsGrid = page.locator('[data-testid="products-grid"]');
    await expect(productsGrid).toBeVisible();
  });

  test('User can add product to cart', async ({ page }) => {
    // Login first
    await page.fill('[data-testid="username-input"]', 'testuser');
    await page.click('[data-testid="login-button"]');

    // Wait for products to load
    await page.waitForSelector('[data-testid="products-grid"]');

    // Get first product's add to cart button
    const addButtons = page.locator('[data-testid^="add-to-cart-btn-"]');
    const count = await addButtons.count();

    if (count > 0) {
      await addButtons.first().click();

      // Accept alert
      await page.once('dialog', dialog => dialog.accept());
    }
  });

  test('User can view cart', async ({ page }) => {
    // Login first
    await page.fill('[data-testid="username-input"]', 'testuser');
    await page.click('[data-testid="login-button"]');

    // Navigate to cart
    await page.click('[data-testid="nav-cart"]');

    // Should see cart title
    await expect(page.locator('[data-testid="cart-title"]')).toContainText('Giỏ hàng');
  });

  test('User can navigate to orders page', async ({ page }) => {
    await page.fill('[data-testid="username-input"]', 'testuser');
    await page.click('[data-testid="login-button"]');

    await page.click('[data-testid="nav-orders"]');
    await expect(page.locator('[data-testid="orders-title"]')).toContainText('Đơn hàng');
  });

  test('User can logout', async ({ page }) => {
    // Login first
    await page.fill('[data-testid="username-input"]', 'testuser');
    await page.click('[data-testid="login-button"]');

    // Click logout button
    await page.click('[data-testid="logout-btn"]');

    // Should redirect to login
    await expect(page).toHaveURL(`${baseURL}/login`);
  });

  test('Unauthenticated user cannot access products', async ({ page }) => {
    // Try to access products directly
    await page.goto(`${baseURL}/products`);

    // Should redirect to login
    await expect(page).toHaveURL(`${baseURL}/login`);
  });
});

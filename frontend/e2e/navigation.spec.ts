import { test, expect } from '@playwright/test';

test.describe('Navigation', () => {
  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto('/login');
    await page.getByLabel(/email/i).fill('admin@inmobiliaria.com');
    await page.getByLabel(/contraseña/i).fill('admin123');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();

    // Wait for navigation to complete
    await page.waitForURL(/\/(dashboard|empresa)/, { timeout: 10000 });
  });

  test('should display main navigation menu', async ({ page }) => {
    // Look for navigation items in the drawer/sidebar
    await expect(page.getByRole('link', { name: /personas/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /propiedades/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /contratos/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /pagos/i })).toBeVisible();
  });

  test('should navigate to personas page', async ({ page }) => {
    await page.getByRole('link', { name: /personas/i }).click();

    await expect(page).toHaveURL(/\/personas/);
    await expect(page.getByRole('heading', { name: /personas/i })).toBeVisible();
  });

  test('should navigate to propiedades page', async ({ page }) => {
    await page.getByRole('link', { name: /propiedades/i }).click();

    await expect(page).toHaveURL(/\/propiedades/);
    await expect(page.getByRole('heading', { name: /propiedades/i })).toBeVisible();
  });

  test('should navigate to contratos page', async ({ page }) => {
    await page.getByRole('link', { name: /contratos/i }).click();

    await expect(page).toHaveURL(/\/contratos/);
    await expect(page.getByRole('heading', { name: /contratos/i })).toBeVisible();
  });

  test('should navigate to pagos page', async ({ page }) => {
    await page.getByRole('link', { name: /pagos/i }).click();

    await expect(page).toHaveURL(/\/pagos/);
    await expect(page.getByRole('heading', { name: /pagos/i })).toBeVisible();
  });

  test('should navigate to cobranza page', async ({ page }) => {
    await page.getByRole('link', { name: /cobranza/i }).click();

    await expect(page).toHaveURL(/\/cobranza/);
  });

  test('should navigate to reportes page', async ({ page }) => {
    await page.getByRole('link', { name: /reportes/i }).click();

    await expect(page).toHaveURL(/\/reportes/);
  });
});

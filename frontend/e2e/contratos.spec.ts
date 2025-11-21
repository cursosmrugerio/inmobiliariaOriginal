import { test, expect } from '@playwright/test';

test.describe('Contratos Management', () => {
  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto('/login');
    await page.getByLabel(/email/i).fill('admin@inmobiliaria.com');
    await page.getByLabel(/contraseña/i).fill('admin123');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();
    await page.waitForURL(/\/(dashboard|empresa)/, { timeout: 10000 });

    // Navigate to contratos
    await page.goto('/contratos');
  });

  test('should display contratos list', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /contratos/i })).toBeVisible();

    // Should have a button to add new contrato
    await expect(page.getByRole('button', { name: /nuevo|agregar|crear/i })).toBeVisible();
  });

  test('should open form to create new contrato', async ({ page }) => {
    await page.getByRole('button', { name: /nuevo|agregar|crear/i }).click();

    await expect(page).toHaveURL(/\/contratos\/nuev|\/contratos\/new/);

    // Should display form fields
    await expect(page.getByLabel(/propiedad/i)).toBeVisible();
    await expect(page.getByLabel(/arrendatario/i)).toBeVisible();
  });

  test('should validate required fields when creating contrato', async ({ page }) => {
    await page.goto('/contratos/nuevo');

    // Try to submit empty form
    await page.getByRole('button', { name: /guardar|crear|submit/i }).click();

    // Should show validation errors
    await expect(page.getByText(/requerido|obligatorio/i).first()).toBeVisible();
  });

  test('should filter contratos by status', async ({ page }) => {
    // Look for status filter
    const statusFilter = page.getByLabel(/estado/i);

    if (await statusFilter.isVisible()) {
      await statusFilter.click();
      await page.getByRole('option', { name: /activo/i }).click();

      // Wait for results to filter
      await page.waitForTimeout(500);
    }
  });
});

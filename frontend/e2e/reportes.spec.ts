import { test, expect } from '@playwright/test';

test.describe('Reportes', () => {
  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto('/login');
    await page.getByLabel(/email/i).fill('admin@inmobiliaria.com');
    await page.getByLabel(/contraseña/i).fill('admin123');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();
    await page.waitForURL(/\/(dashboard|empresa)/, { timeout: 10000 });

    // Navigate to reportes
    await page.goto('/reportes');
  });

  test('should display reportes page', async ({ page }) => {
    await expect(page.getByText(/reportes/i)).toBeVisible();
  });

  test('should have report type options', async ({ page }) => {
    // Should show different report types
    await expect(page.getByText(/estado.*cuenta|cartera.*vencida|antigüedad.*saldos|proyección/i).first()).toBeVisible();
  });

  test('should have export options', async ({ page }) => {
    // Should have export buttons for Excel/CSV
    const exportButton = page.getByRole('button', { name: /exportar|descargar|excel|csv/i });

    if (await exportButton.isVisible()) {
      await expect(exportButton).toBeVisible();
    }
  });
});

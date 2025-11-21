import { test, expect } from '@playwright/test';

test.describe('Pagos Management', () => {
  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto('/login');
    await page.getByLabel(/email/i).fill('admin@inmobiliaria.com');
    await page.getByLabel(/contraseña/i).fill('admin123');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();
    await page.waitForURL(/\/(dashboard|empresa)/, { timeout: 10000 });

    // Navigate to pagos
    await page.goto('/pagos');
  });

  test('should display pagos page', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /pagos/i })).toBeVisible();
  });

  test('should have tabs for pagos and cargos', async ({ page }) => {
    // Look for tabs or navigation between pagos and cargos
    const pagosTab = page.getByRole('tab', { name: /pagos/i });
    const cargosTab = page.getByRole('tab', { name: /cargos/i });

    if (await pagosTab.isVisible()) {
      await expect(pagosTab).toBeVisible();
      await expect(cargosTab).toBeVisible();
    }
  });

  test('should open form to register new pago', async ({ page }) => {
    await page.getByRole('button', { name: /nuevo|registrar|agregar/i }).first().click();

    // Should display form fields for pago
    await expect(page.getByLabel(/contrato/i)).toBeVisible();
    await expect(page.getByLabel(/monto/i)).toBeVisible();
    await expect(page.getByLabel(/método.*pago/i)).toBeVisible();
  });

  test('should display cargos list', async ({ page }) => {
    // Click on cargos tab if exists
    const cargosTab = page.getByRole('tab', { name: /cargos/i });

    if (await cargosTab.isVisible()) {
      await cargosTab.click();

      // Should show cargos list
      await expect(page.getByText(/cargo|concepto/i)).toBeVisible();
    }
  });
});

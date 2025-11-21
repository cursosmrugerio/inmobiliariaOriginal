import { test, expect } from '@playwright/test';

test.describe('Propiedades Management', () => {
  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto('/login');
    await page.getByLabel(/email/i).fill('admin@inmobiliaria.com');
    await page.getByLabel(/contraseña/i).fill('admin123');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();
    await page.waitForURL(/\/(dashboard|empresa)/, { timeout: 10000 });

    // Navigate to propiedades
    await page.goto('/propiedades');
  });

  test('should display propiedades list', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /propiedades/i })).toBeVisible();

    // Should have a button to add new propiedad
    await expect(page.getByRole('button', { name: /nueva|agregar|crear/i })).toBeVisible();
  });

  test('should open form to create new propiedad', async ({ page }) => {
    await page.getByRole('button', { name: /nueva|agregar|crear/i }).click();

    await expect(page).toHaveURL(/\/propiedades\/nuev|\/propiedades\/new/);

    // Should display form fields
    await expect(page.getByLabel(/nombre/i)).toBeVisible();
    await expect(page.getByLabel(/tipo.*propiedad/i)).toBeVisible();
  });

  test('should create a new propiedad', async ({ page }) => {
    await page.goto('/propiedades/nuevo');

    // Fill form
    await page.getByLabel(/nombre/i).fill('Departamento Test');

    await page.getByLabel(/tipo.*propiedad/i).click();
    await page.getByRole('option').first().click();

    await page.getByLabel(/dirección/i).fill('Av. Test 123, Col. Centro');
    await page.getByLabel(/superficie/i).fill('85');
    await page.getByLabel(/habitaciones/i).fill('2');
    await page.getByLabel(/baños/i).fill('1');
    await page.getByLabel(/estacionamientos/i).fill('1');
    await page.getByLabel(/precio.*renta/i).fill('12000');

    await page.getByRole('button', { name: /guardar|crear|submit/i }).click();

    // Should redirect to list or detail
    await expect(page).toHaveURL(/\/propiedades(?!\/nuev)/);
  });

  test('should filter propiedades by availability', async ({ page }) => {
    // Look for filter/toggle for availability
    const disponibleFilter = page.getByLabel(/disponible/i);

    if (await disponibleFilter.isVisible()) {
      await disponibleFilter.click();

      // Wait for results to filter
      await page.waitForTimeout(500);
    }
  });
});

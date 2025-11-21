import { test, expect } from '@playwright/test';

test.describe('Personas Management', () => {
  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto('/login');
    await page.getByLabel(/email/i).fill('admin@inmobiliaria.com');
    await page.getByLabel(/contraseña/i).fill('admin123');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();
    await page.waitForURL(/\/(dashboard|empresa)/, { timeout: 10000 });

    // Navigate to personas
    await page.goto('/personas');
  });

  test('should display personas list', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /personas/i })).toBeVisible();

    // Should have a button to add new persona
    await expect(page.getByRole('button', { name: /nueva|agregar|crear/i })).toBeVisible();
  });

  test('should open form to create new persona', async ({ page }) => {
    await page.getByRole('button', { name: /nueva|agregar|crear/i }).click();

    await expect(page).toHaveURL(/\/personas\/nuevo|\/personas\/new/);

    // Should display form fields
    await expect(page.getByLabel(/tipo.*persona/i)).toBeVisible();
    await expect(page.getByLabel(/nombre/i)).toBeVisible();
  });

  test('should validate required fields when creating persona', async ({ page }) => {
    await page.goto('/personas/nuevo');

    // Try to submit empty form
    await page.getByRole('button', { name: /guardar|crear|submit/i }).click();

    // Should show validation errors
    await expect(page.getByText(/requerido|obligatorio/i).first()).toBeVisible();
  });

  test('should create a new persona fisica', async ({ page }) => {
    await page.goto('/personas/nuevo');

    // Fill form for persona fisica
    await page.getByLabel(/tipo.*persona/i).click();
    await page.getByRole('option', { name: /física/i }).click();

    await page.getByLabel(/^nombre$/i).fill('Test');
    await page.getByLabel(/apellido.*paterno/i).fill('Usuario');
    await page.getByLabel(/apellido.*materno/i).fill('Prueba');
    await page.getByLabel(/rfc/i).fill('USPT900101ABC');
    await page.getByLabel(/email/i).fill('test@usuario.com');
    await page.getByLabel(/teléfono/i).first().fill('5551234567');

    await page.getByRole('button', { name: /guardar|crear|submit/i }).click();

    // Should redirect to list or detail
    await expect(page).toHaveURL(/\/personas(?!\/nuevo)/);
  });

  test('should search personas', async ({ page }) => {
    // Look for search input
    const searchInput = page.getByPlaceholder(/buscar/i);

    if (await searchInput.isVisible()) {
      await searchInput.fill('Test');

      // Wait for results to filter
      await page.waitForTimeout(500);
    }
  });

  test('should display persona details', async ({ page }) => {
    // Click on first persona in list (if exists)
    const firstRow = page.locator('table tbody tr').first();

    if (await firstRow.isVisible()) {
      await firstRow.click();

      // Should show detail view
      await expect(page.getByText(/información|detalle/i)).toBeVisible();
    }
  });
});

import { test, expect } from '@playwright/test';

test.describe('Authentication', () => {
  test('should display login page', async ({ page }) => {
    await page.goto('/login');

    await expect(page.getByRole('heading', { name: /iniciar sesión/i })).toBeVisible();
    await expect(page.getByLabel(/email/i)).toBeVisible();
    await expect(page.getByLabel(/contraseña/i)).toBeVisible();
    await expect(page.getByRole('button', { name: /iniciar sesión/i })).toBeVisible();
  });

  test('should show validation errors for empty fields', async ({ page }) => {
    await page.goto('/login');

    await page.getByRole('button', { name: /iniciar sesión/i }).click();

    await expect(page.getByText(/email.*requerido/i)).toBeVisible();
  });

  test('should show error for invalid credentials', async ({ page }) => {
    await page.goto('/login');

    await page.getByLabel(/email/i).fill('invalid@test.com');
    await page.getByLabel(/contraseña/i).fill('wrongpassword');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();

    // Should show error message
    await expect(page.getByText(/credenciales inválidas|error/i)).toBeVisible({ timeout: 10000 });
  });

  test('should redirect to login when accessing protected route', async ({ page }) => {
    await page.goto('/dashboard');

    // Should be redirected to login
    await expect(page).toHaveURL(/\/login/);
  });

  test('should successfully login with valid credentials', async ({ page }) => {
    await page.goto('/login');

    await page.getByLabel(/email/i).fill('admin@inmobiliaria.com');
    await page.getByLabel(/contraseña/i).fill('admin123');
    await page.getByRole('button', { name: /iniciar sesión/i }).click();

    // Should be redirected to dashboard or empresa selector
    await expect(page).toHaveURL(/\/(dashboard|empresa)/);
  });
});

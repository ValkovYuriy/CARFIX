import { test, expect } from '@playwright/test';

/**
 * E2E-автоматизация тест-кейса №1 (практическая работа №4):
 * «Регистрация пользователя проходит успешно».
 * Проверяет интеграцию фронтенда и бэкенда по полному пользовательскому сценарию:
 * регистрация -> редирект на главную -> выход -> повторный вход.
 */
test.describe('Аутентификация (UI)', () => {
  const suffix = Date.now();
  const user = {
    email: `e2e.user.${suffix}@mail.ru`,
    password: 'e2ePass123',
    firstName: 'Тест',
    lastName: 'Тестов',
    phone: '9991234567',
  };

  test('ТК-1: регистрация, выход и повторный вход', async ({ page }) => {
    // Шаги 1-6: регистрация
    await page.goto('/register');
    await page.locator('#email').fill(user.email);
    await page.locator('#password').fill(user.password);
    await page.locator('#confirmPassword').fill(user.password);
    await page.locator('#phone-number').fill(user.phone);
    await page.locator('input[formControlName="firstName"]').fill(user.firstName);
    await page.locator('input[formControlName="lastName"]').fill(user.lastName);
    await page.locator('input[formControlName="agreeToTerms"]').check();
    await page.getByRole('button', { name: 'Зарегистрироваться' }).click();

    // Ожидаемый результат: редирект на главную, в шапке имя и фамилия
    await expect(page).toHaveURL(/\/home/);
    await expect(page.locator('#dropdownMenuButton'))
      .toContainText(`${user.firstName} ${user.lastName}`);

    // Шаг 7: выход из аккаунта
    await page.locator('#dropdownMenuButton').click();
    await page.getByRole('link', { name: 'Выйти', exact: true }).click();
    await expect(page).toHaveURL(/\/login/);

    // Шаг 8: главная страница доступна анонимно, пункты «Войти»/«Зарегистрироваться» видны
    await page.goto('/home');
    await expect(page.getByRole('link', { name: 'Войти', exact: true })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Зарегистрироваться' })).toBeVisible();

    // Шаги 9-11: повторный вход с теми же почтой и паролем
    await page.goto('/login');
    await page.locator('#email').fill(user.email);
    await page.locator('#password').fill(user.password);
    await page.getByRole('button', { name: 'Войти' }).click();

    await expect(page).toHaveURL(/\/home/);
    await expect(page.locator('#dropdownMenuButton'))
      .toContainText(`${user.firstName} ${user.lastName}`);
  });

  test('Вход с неверным паролем отклоняется', async ({ page }) => {
    await page.goto('/login');
    await page.locator('#email').fill(user.email);
    await page.locator('#password').fill('wrong-password');
    await page.getByRole('button', { name: 'Войти' }).click();

    // Остаёмся на странице входа, главная не открывается
    await expect(page).toHaveURL(/\/login/);
    await expect(page.locator('#dropdownMenuButton')).toHaveCount(0);
  });
});

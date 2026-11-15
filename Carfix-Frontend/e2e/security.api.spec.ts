import { test, expect } from '@playwright/test';

/**
 * E2E-автоматизация тест-кейса №3 (практическая работа №4):
 * «Отправка запросов без полномочий администратора».
 * Пользователь с ролью USER не должен создавать, изменять и удалять услуги;
 * неаутентифицированный запрос к защищённым ресурсам отклоняется.
 */
test.describe('Ролевая модель доступа (API фронтенда)', () => {
  const suffix = Date.now();
  let token: string;

  test.beforeAll(async ({ request }) => {
    const register = await request.post('/api/auth/register', {
      data: {
        username: `e2e.sec.${suffix}@mail.ru`,
        password: 'e2ePass123',
        firstName: 'Тест',
        lastName: 'Тестов',
        phoneNumber: '+79991234567',
      },
    });
    expect(register.ok()).toBeTruthy();
    token = (await register.json()).token;
  });

  test('ТК-3: создание услуги с ролью USER -> 403', async ({ request }) => {
    const resp = await request.post('/api/works', {
      headers: { Authorization: `Bearer ${token}` },
      data: { name: 'test', description: 'test', workPrice: 1000 },
    });
    expect(resp.status()).toBe(403);
  });

  test('ТК-3: изменение услуги с ролью USER -> 403', async ({ request }) => {
    const resp = await request.put('/api/works/00000000-0000-0000-0000-000000000000', {
      headers: { Authorization: `Bearer ${token}` },
      data: { name: 'test', description: 'test', workPrice: 1000 },
    });
    expect(resp.status()).toBe(403);
  });

  test('ТК-3: удаление услуги с ролью USER -> 403', async ({ request }) => {
    const resp = await request.delete('/api/works/00000000-0000-0000-0000-000000000000', {
      headers: { Authorization: `Bearer ${token}` },
    });
    expect(resp.status()).toBe(403);
  });

  test('Защищённый ресурс без токена -> 403', async ({ request }) => {
    const resp = await request.get('/api/orders');
    expect(resp.status()).toBe(403);
  });

  test('Публичный каталог услуг доступен без токена -> 200', async ({ request }) => {
    const resp = await request.get('/api/works');
    expect(resp.ok()).toBeTruthy();
  });
});

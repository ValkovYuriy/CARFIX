import { test, expect } from '@playwright/test';

/**
 * E2E-автоматизация тест-кейса №2 (практическая работа №4) на уровне REST API,
 * которым пользуется фронтенд: «Оставить заявку на обслуживание».
 * Сценарий воспроизводит контракт фронтенда: регистрация -> получение каталога
 * услуг и моделей -> создание заявки -> заявка видна в истории -> детали заявки.
 * Дополнительно контролирует регрессию дефекта #CARFIX-00004 (413 Request Entity
 * Too Large при POST /api/orders): ответ обязан быть 200 OK.
 */
test.describe('Заявки на обслуживание (API фронтенда)', () => {
  const suffix = Date.now();
  const email = `e2e.order.${suffix}@mail.ru`;
  let token: string;
  let orderId: string;

  test('ТК-2: создание заявки и её отображение в истории', async ({ request }) => {
    // Регистрация пользователя (как из формы фронтенда)
    const register = await request.post('/api/auth/register', {
      data: {
        username: email,
        password: 'e2ePass123',
        firstName: 'Тест',
        lastName: 'Тестов',
        phoneNumber: '+79991234567',
      },
    });
    expect(register.ok()).toBeTruthy();
    token = (await register.json()).token;
    expect(token).toBeTruthy();
    const auth = { Authorization: `Bearer ${token}` };

    // Каталог услуг (публичный, используется формой записи)
    const worksResp = await request.get('/api/works');
    expect(worksResp.ok()).toBeTruthy();
    const works = (await worksResp.json()).data;
    expect(works.length).toBeGreaterThan(0);
    const chosenWorks = works.slice(0, 2);

    // Справочник моделей (подтягивается по VIN на фронтенде)
    const modelsResp = await request.get('/api/models', { headers: auth });
    expect(modelsResp.ok()).toBeTruthy();
    const models = (await modelsResp.json()).data;
    expect(models.length).toBeGreaterThan(0);

    // Создание заявки — контракт POST /api/orders?timezoneOffset=...
    const orderDate = new Date(Date.now() + 24 * 60 * 60 * 1000)
      .toISOString()
      .slice(0, 19);
    const create = await request.post('/api/orders?timezoneOffset=-240', {
      headers: auth,
      data: {
        carDto: {
          govNumber: `Е${String(suffix).slice(-3)}КХ163`,
          vinNumber: `E2E${suffix}`.slice(0, 17),
          modelDto: models[0],
        },
        userDto: null,
        orderDate,
        price: 0,
        status: 'PENDING',
        description: 'E2E тестовая заявка',
        works: chosenWorks,
      },
    });
    // #CARFIX-00004: заявка должна успешно создаваться (ранее — 413 Request Entity Too Large)
    expect(create.status()).toBe(200);
    const created = (await create.json()).data;
    orderId = created.id;
    expect(orderId).toBeTruthy();
    expect(created.status).toBe('PENDING');

    // История обслуживания: id пользователя извлекается из JWT (как делает фронтенд)
    const payload = JSON.parse(
      Buffer.from(token.split('.')[1], 'base64').toString('utf-8'),
    );
    const history = await request.get(`/api/orders?userId=${payload.id}`, {
      headers: auth,
    });
    expect(history.ok()).toBeTruthy();
    const historyData = (await history.json()).data;
    expect(historyData.map((o: any) => o.id)).toContain(orderId);

    // Детали заявки (кнопка «Подробнее» в личном кабинете)
    const details = await request.get(`/api/orders/${orderId}`, { headers: auth });
    expect(details.ok()).toBeTruthy();
    const detailsData = (await details.json()).data;
    expect(detailsData.description).toBe('E2E тестовая заявка');
    expect(detailsData.works.length).toBe(chosenWorks.length);

    // Очистка тестовых данных.
    // ВНИМАНИЕ: обнаружен дефект — DELETE /api/orders/{id} возвращает 500 для заказа
    // с услугами (CascadeType.REMOVE на ManyToMany пытается удалить справочные Work,
    // FK-нарушение SQLState 23503). До исправления дефекта очистка выполняется
    // в режиме best-effort, а факт дефекта фиксируется в отчёте ИФТ.
    const cleanup = await request.delete(`/api/orders/${orderId}`, { headers: auth });
    if (!cleanup.ok()) {
      console.warn(
        `Известный дефект: удаление заказа ${orderId} вернуло ${cleanup.status()} (FK 23503)`,
      );
    }
  });
});

import { defineConfig } from '@playwright/test';

/**
 * Конфигурация E2E-тестов интеграции фронтенда и бэкенда (стратегия тестирования, п. «E2E: Playwright»).
 * Перед запуском должны быть подняты:
 *   - backend: http://localhost:8080 (mvn spring-boot:run в Carfix-Backend)
 *   - frontend: http://localhost:4200 (npm run start:e2e)
 * Браузер: установленный Google Chrome (channel: 'chrome'), скачивание Chromium не требуется.
 */
export default defineConfig({
  testDir: './e2e',
  timeout: 60_000,
  expect: { timeout: 15_000 },
  retries: 0,
  workers: 1,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://localhost:4200',
    channel: 'chrome',
    headless: true,
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
});

#!/usr/bin/env bash
# Запуск E2E-тестов интеграции фронтенда и бэкенда (Playwright).
# Поднимает backend (8080) и frontend (4200), прогоняет тесты, останавливает серверы.
# Использование: ./run-e2e.sh
set -u
cd "$(dirname "$0")"

export JAVA_HOME="${JAVA_HOME:-C:\Users\User\.jdks\corretto-21.0.6}"
MVN="/c/Program Files/JetBrains/IntelliJIdea2024.1/plugins/maven/lib/maven3/bin/mvn"

# Переменные окружения (не выводятся в лог):
#  - KEY/ENCRYPT_KEY/MAIL_PASS — из .env (значения с пробелами и спецсимволами читаем построчно)
#  - DB_URL/DB_USER/DB_PASSWORD — из run-конфигурации IDEA (.idea/workspace.xml), там рабочие локальные креды БД
while IFS='=' read -r k v; do
  v="${v%\"}"; v="${v#\"}"; v="${v%$'\r'}"
  export "$k=$v"
done < .env

extract_env() {
  grep -o "name=\"$1\" value=\"[^\"]*\"" .idea/workspace.xml | head -1 | sed "s/^name=\"$1\" value=\"//; s/\"$//"
}
export DB_URL="$(extract_env DB_URL)"
export DB_USER="$(extract_env DB_USER)"
export DB_PASSWORD="$(extract_env DB_PASSWORD)"
[ -n "$DB_URL" ] || { echo "DB_URL не найден в .idea/workspace.xml"; exit 1; }

BACKEND_LOG=/tmp/carfix-backend-e2e.log
FRONTEND_LOG=/tmp/carfix-frontend-e2e.log
BACKEND_PID=""
FRONTEND_PID=""

cleanup() {
  [ -n "$BACKEND_PID" ] && kill "$BACKEND_PID" 2>/dev/null
  [ -n "$FRONTEND_PID" ] && kill "$FRONTEND_PID" 2>/dev/null
  sleep 2
  [ -n "$BACKEND_PID" ] && kill -9 "$BACKEND_PID" 2>/dev/null
  [ -n "$FRONTEND_PID" ] && kill -9 "$FRONTEND_PID" 2>/dev/null
}
trap cleanup EXIT

wait_for() {
  local url="$1" tries="$2" name="$3"
  for i in $(seq 1 "$tries"); do
    if curl -sf -o /dev/null "$url"; then echo "[ok] $name доступен: $url"; return 0; fi
    sleep 3
  done
  echo "[fail] $name не поднялся: $url"
  return 1
}

echo "==> Сборка backend (skip tests)"
"$MVN" -q -B -f Carfix-Backend/pom.xml -DskipTests package || { echo "Сборка backend не удалась"; exit 1; }

echo "==> Запуск backend"
"$JAVA_HOME/bin/java" -jar Carfix-Backend/target/Carfix-Backend-0.0.1-SNAPSHOT.jar > "$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!
# В качестве пробы готовности используем публичный каталог услуг:
# /actuator/health не подходит — mail-индикатор даёт 503 при отсутствии валидного SMTP-пароля
wait_for "http://localhost:8080/api/works" 40 "Backend" || { tail -30 "$BACKEND_LOG"; exit 1; }

echo "==> Запуск frontend (ng serve + proxy на :8080)"
cd Carfix-Frontend
npx ng serve --port 4200 --proxy-config proxy.e2e.json > "$FRONTEND_LOG" 2>&1 &
FRONTEND_PID=$!
wait_for "http://localhost:4200" 60 "Frontend" || { tail -30 "$FRONTEND_LOG"; exit 1; }

echo "==> Прогон Playwright E2E-тестов"
npx playwright test
E2E_STATUS=$?

echo "==> E2E завершены со статусом $E2E_STATUS"
exit $E2E_STATUS

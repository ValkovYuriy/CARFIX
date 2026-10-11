# Этап сборки
FROM node:22-alpine AS build

WORKDIR /app

# Копируем зависимости
COPY package*.json ./
RUN npm install

# Копируем исходники
COPY . .

# Собираем проект
# Важно: убираем --prod (устарело) и proxy-config (не нужен при сборке)
RUN npm run build -- --configuration production

# Этап деплоя
FROM nginx:alpine

COPY --from=build /app/dist/carfix-frontend/browser /usr/share/nginx/html

# Копируем ваш конфиг nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Получите токен из локального хранилища
  const token = localStorage.getItem('token'); // Измените путь к токену, если нужно

  // Проверяйте, существует ли токен
  if (token) {
    // Измените запрос, добавив заголовок Authorization
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}` // Добавление токена в заголовок
      }
    });

    // Продолжайте обработку с изменённым запросом
    return next(clonedReq);
  }

  // Если токена нет, отправьте оригинальный запрос без изменений
  return next(req);
};

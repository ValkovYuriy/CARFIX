import { CanActivateFn } from '@angular/router';
import {AuthenticationService} from '../services/AuthenticationService/authentication.service';
import {inject} from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthenticationService); // Не забудьте правильно инициализировать ваш AuthService
  return authService.isTokenValid();
};

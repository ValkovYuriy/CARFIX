import {CanActivateFn, Router} from '@angular/router';
import {AuthenticationService} from '../services/AuthenticationService/authentication.service';
import {inject} from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  if (authService.isTokenValid()) {
    return true;
  }

  authService.logout();

  return router.createUrlTree(['/login']);
};

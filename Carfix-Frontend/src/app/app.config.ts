import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideEnvironmentNgxMask} from 'ngx-mask';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptor} from './interceptor/auth.interceptor';
import { provideNzIcons } from 'ng-zorro-antd/icon';
import { UserOutline, CarOutline, CheckCircleOutline } from '@ant-design/icons-angular/icons';

import {MAT_DATE_LOCALE, provideNativeDateAdapter} from '@angular/material/core';
import {provideAnimations} from '@angular/platform-browser/animations';

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'ru-RU' },
    provideAnimations(),
    provideNativeDateAdapter(),
    provideNzIcons([UserOutline,CarOutline,CheckCircleOutline]),
    provideEnvironmentNgxMask(),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes)]
};

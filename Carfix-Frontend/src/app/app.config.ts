import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideEnvironmentNgxMask} from 'ngx-mask';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptor} from './interceptor/auth.interceptor';
import { provideNzIcons } from 'ng-zorro-antd/icon';
import { UserOutline, CarOutline, CheckCircleOutline } from '@ant-design/icons-angular/icons';

import {provideImgixLoader} from '@angular/common';
import {TooltipModule} from 'ngx-bootstrap/tooltip';

export const appConfig: ApplicationConfig = {
  providers: [
    provideNzIcons([UserOutline,CarOutline,CheckCircleOutline]),
    provideEnvironmentNgxMask(),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes)]
};

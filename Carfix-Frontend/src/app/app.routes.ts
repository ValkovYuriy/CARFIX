import { Routes } from '@angular/router';
import {authGuard} from './guard/auth.guard';
import {LkComponent} from './components/lk/lk.component';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';
import {
  RegistrationForServiceComponent
} from './components/registration-for-service/registration-for-service.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent},
  { path: 'register', component: RegisterComponent},
  { path: 'lk', component: LkComponent, canActivate: [authGuard]},
  { path: 'registration-for-service', component: RegistrationForServiceComponent, canActivate: [authGuard]}
];


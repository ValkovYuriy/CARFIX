import { Routes } from '@angular/router';
import {authGuard} from './guard/auth.guard';
import {LkComponent} from './components/lk/lk.component';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';
import {
  RegistrationForServiceComponent
} from './components/registration-for-service/registration-for-service.component';
import {ReviewsComponent} from './components/reviews/reviews.component';
import {AppComponent} from './app.component';
import {PrivacyPolicyComponent} from './components/privacy-policy/privacy-policy.component';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full'},
  { path: 'home', component: AppComponent},
  { path: 'login', component: LoginComponent},
  { path: 'register', component: RegisterComponent},
  { path: 'privacy-policy', component: PrivacyPolicyComponent},
  { path: 'reviews', component: ReviewsComponent},
  { path: 'lk', component: LkComponent, canActivate: [authGuard]},
  { path: 'registration-for-service', component: RegistrationForServiceComponent, canActivate: [authGuard]}
];


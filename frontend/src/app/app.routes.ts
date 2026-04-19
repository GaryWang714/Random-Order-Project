import { Routes } from '@angular/router';
import { App } from './app';
import { LoginComponent } from './login/login';
import { OrdersComponent } from './orders/orders';
import { RegisterComponent } from './register/register';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
  { path: '', component: OrdersComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];

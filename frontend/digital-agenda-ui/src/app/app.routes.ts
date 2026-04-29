import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [

  // Rotte pubbliche
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component')
      .then(m => m.RegisterComponent) 
    },
      
    { 
    path: 'login-success', 
    loadComponent: () => import('./features/auth/login-success/login-success.component')
      .then(m => m.LoginSuccessComponent) 
  },

  // Rotte private sotto il MainLayout
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/main-layout/main-layout.component')
      .then(m => m.MainLayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/professional-dashboard/dashboard/dashboard.component')
          .then(m => m.DashboardComponent)
      },
      {
        path: 'booking',
        loadComponent: () => import('./features/client-booking/booking/booking.component')
          .then(m => m.BookingComponent)
      },

            {
        path: 'upgrade',
        loadComponent: () => import('./features/profile/upgrade/upgrade.component')
          .then(m => m.UpgradeComponent)
      },

            {
        path: 'profile',
        loadComponent: () => import('./features/profile/edit-profile/edit-profile.component')
          .then(m => m.EditProfileComponent)
      },
    ]
  },

  // rotte per il recupero e reset della password
  {path: 'forgot-password',
    loadComponent: () => import('./features/auth/forgot-password/forgot-password.component')
      .then(m => m.ForgotPasswordComponent)
  },

  {
    // NOTA questa rotta accetta un parametro 'token' in query string (es. /reset-password?token=123)
    path: 'reset-password',
    loadComponent: () => import('./features/auth/reset-password/reset-password.component')
      .then(m => m.ResetPasswordComponent)
  },

  // Wildcard 
  { path: '**', redirectTo: 'login' },

  

];

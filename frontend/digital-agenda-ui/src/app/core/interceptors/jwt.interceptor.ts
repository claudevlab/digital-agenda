import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // in un interceptor funzionale , iniettiamo i servizi con 'inject'
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Controlliamo se l'URL contiene "/api/auth"
  const isAuthUrl = req.url.includes('/api/auth');

    console.log('Interceptor URL:', req.url, '| Token:', token ? 'PRESENTE' : 'ASSENTE');

    // Aggiungiamo il token SOLO se c'è un token e NON stiamo chiamando login/register
  if (token && typeof token === 'string' && token.trim() !== '' && !isAuthUrl) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  
  return next(req);
};
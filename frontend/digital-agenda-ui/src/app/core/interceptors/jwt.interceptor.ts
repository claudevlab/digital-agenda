import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  
  const authService = inject(AuthService);
  const token = authService.getToken();
  
   console.log('Interceptor URL:', req.url, '| Token:', token ? 'PRESENTE' : 'ASSENTE');
  
// Controlliamo se l'URL contiene "/api/auth"
  const isAuthUrl = req.url.includes('/api/auth');
  
if (token && typeof token === 'string' && token.trim() !== '' && !isAuthUrl) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  return next(req);
};
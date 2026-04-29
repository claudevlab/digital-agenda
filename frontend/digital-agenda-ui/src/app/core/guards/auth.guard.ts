import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// functional guard ( standard moderno)
export const authGuard: CanActivateFn = (route, state) => {
  // injection dei services
  const authService = inject(AuthService);
  const router = inject(Router)

  // controllo se esiste il token
  if(authService.getToken()) {
    // autenticato --> lascialo passare
    return true;
  }

  // NON autenticato --> redirect al login
  router.navigate([ '/login' ], { queryParams: { returnUrl : state.url } } );

  return false;
};

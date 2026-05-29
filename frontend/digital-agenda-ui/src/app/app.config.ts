
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { ErrorLoggingInterceptor } from './core/interceptors/error-logging.interceptor';

export const appConfig : ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
   
    //  REGISTRO L'INTERCEPTOR
   // Configuriamo il client HTTP per utilizzare la Fetch API nativa del browser, che è più moderna e performante rispetto a XMLHttpRequest. Inoltre, aggiungiamo il nostro interceptor personalizzato (jwtInterceptor) per includere automaticamente il token JWT nelle richieste al backend, migliorando così la sicurezza e l'efficienza delle comunicazioni tra frontend e backend.
    provideHttpClient(
      withFetch(), // Usa la Fetch API nativa del browser (più moderna di XHR)
      withInterceptors([jwtInterceptor])
    ),

    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, 
      useClass: ErrorLoggingInterceptor, 
      multi: true }

    
  ]

  
};



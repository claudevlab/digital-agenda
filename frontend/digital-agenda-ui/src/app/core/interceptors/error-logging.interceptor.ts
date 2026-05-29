import { Injectable } from '@angular/core';
import { 
  HttpRequest, 
  HttpHandler, 
  HttpEvent, 
  HttpInterceptor, 
  HttpErrorResponse 
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoggingService } from '../services/logging.service';

@Injectable()
export class ErrorLoggingInterceptor implements HttpInterceptor {

  constructor(private loggingService: LoggingService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        //  NON loggare errori su /api/logs/error (evita loop)
        if (!request.url.includes('/api/logs/error')) {
          this.loggingService.logError(error, `HTTP ${request.method} ${request.url}`);
        }
        
        // Propaga l'errore al component (non bloccare il flusso)
        return throwError(() => error);
      })
    );
  }
}
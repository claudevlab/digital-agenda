// src/app/core/services/logging.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoggingService {

  private logsUrl = `${environment.apiUrl}/api/logs/error`;

  constructor(private http: HttpClient) {}

  logError(error: any, context: string = 'Unknown') {
    // 1. Log in console (solo sviluppo)
    if (!environment.production) {
      console.error(`[${context}] Errore:`, error);
    }

    // 2. Invia al backend
    const errorLog = {
      context,
      message: error.message || 'No message',
      status: error.status || 'N/A',
      statusText: error.statusText || 'N/A',
      url: error.url || 'N/A',
      method: error.method || 'N/A',
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
      stack: error.stack || null,
      errorDetails: error.error || null
    };

    
    // Invia in background (non bloccare l'utente)
    this.http.post(this.logsUrl, errorLog).subscribe({
      error: (loggingErr) => {
        // Ignora fallimento logging (non vogliamo peggiorare la situazione)
        console.warn('Failed to send error log to server:', loggingErr);
      }
    });


    // 3. Salvataggio locale (per debugging)
    this.saveToLocalLog(errorLog);
  }

  private saveToLocalLog(errorLog: any) {
    try {
      const existingLogs = JSON.parse(localStorage.getItem('error_logs') || '[]');
      existingLogs.push(errorLog);
      
      if (existingLogs.length > 50) {
        existingLogs.shift();
      }
      
      localStorage.setItem('error_logs', JSON.stringify(existingLogs));
    } catch (e) {
      console.warn('Failed to save error log to localStorage:', e);
    }
  }

  // recupera log locali per debugging
  getLocalLogs(): any[] {
    try {
      return JSON.parse(localStorage.getItem('error_logs') || '[]');
    } catch {
      return [];
    }
  }

}
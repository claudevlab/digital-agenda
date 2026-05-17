import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppointmentRequestDTO, AppointmentResponseDTO, PagedResponse } from '../models/appointment.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  private apiUrl = `${environment.apiUrl}/api/appointments`;

  constructor(private http: HttpClient) {}

  // POST /api/appointments
  // Cliente: crea un appuntamento
  bookAppointment(request: AppointmentRequestDTO): Observable<AppointmentResponseDTO> {
    return this.http.post<AppointmentResponseDTO>(this.apiUrl, request);
  }

  // GET /api/appointments/customer-appointments
  // Cliente: vede i propri appuntamenti
  getCustomerAppointments(): Observable<AppointmentResponseDTO[]> {
    return this.http.get<AppointmentResponseDTO[]>(`${this.apiUrl}/customer-appointments`);
  }

  // GET /api/appointments/professional-appointments
  // Professionista: vede i propri appuntamenti
  getProfessionalAppointments(): Observable<AppointmentResponseDTO[]> {
    return this.http.get<AppointmentResponseDTO[]>(`${this.apiUrl}/professional-appointments`);
  }

  // Aggiungilo al service esistente, accanto a getProfessionalAppointments()
getHistoricalAppointments(page: number = 0, size: number = 10): Observable<PagedResponse<AppointmentResponseDTO>> {
  return this.http.get<PagedResponse<AppointmentResponseDTO>>(
    `${this.apiUrl}/professional-appointments/historical?page=${page}&size=${size}`
  );
}

  // PUT /api/appointments/{id}/status?status=CONFIRMED|CANCELLED
  // Professionista: accetta o rifiuta
  updateAppointmentStatus(id: number, status: 'CONFIRMED' | 'REJECTED', reasonRejected?: string): Observable<AppointmentResponseDTO> {
    let params = new HttpParams().set('status', status);

    if (reasonRejected && reasonRejected.trim() !== '') {
    params = params.set('reasonRejected', reasonRejected.trim());
    }
    
    return this.http.patch<AppointmentResponseDTO>(`${this.apiUrl}/${id}/status`,{}, { params });
  }

  // DELETE /api/appointments/{id}
  // Cliente: cancella un appuntamento
  cancelAppointment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }


}

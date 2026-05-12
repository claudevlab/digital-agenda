import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { ScheduleExceptionRequest, ScheduleExceptionResponse } from '../models/schedule-exception.model';

@Injectable({
  providedIn: 'root'
})
export class ScheduleExceptionService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/exceptions`;

  // GET /api/exceptions/{professionalId}
  getExceptionsByProfessional(professionalId : number): 
  Observable<ScheduleExceptionResponse[]> {
    return this.http.get<ScheduleExceptionResponse[]>
    (`${this.apiUrl}/${professionalId}`);
  }

  // POST /api/exceptions/{professionalId}
  createException(professionalId : number , request : ScheduleExceptionRequest) :

  Observable<ScheduleExceptionResponse> {
    return this.http.post<ScheduleExceptionResponse>
    (`${this.apiUrl}/${professionalId}`,request)
  }

  // DELETE /api/exceptions/{id}
  deleteException (id : number) :
  Observable<void> {
    return this.http.delete<void> (`${this.apiUrl}/${id}`);
  }
}

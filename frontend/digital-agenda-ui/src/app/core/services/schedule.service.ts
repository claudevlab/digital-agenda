import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AvailableSlotDTO, Schedule, ScheduleRequest , } from '../models/schedule.model';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/schedules`;

  //GET 
  // recupera tutti gli schedule del professionista loggato
  getScheduleByProfessional(professionalId : number) : Observable<Schedule[]> {
    return this.http.get<Schedule[]> (`${this.apiUrl}/${professionalId}`);
  }

  // POST 
  // Crea un nuovo schedule per il professionista logggato
  createSchedule(professionalId : number , request : ScheduleRequest) :
  Observable<Schedule> {
    return this.http.post<Schedule>(`${this.apiUrl}/${professionalId}`,request)
  }

  // DELETE 
  //Elimina uno schedule per id
  deleteSchedule (id : number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }

  //OTTIENI SLOT LIBERI
  getAvailableSlots(professionalId: number, date: string, durationMinutes : number): Observable<AvailableSlotDTO[]> {
  return this.http.get<AvailableSlotDTO[]>(`${this.apiUrl}/available-slots`, {
    params: { professionalId: professionalId.toString(), date , durationMinutes : durationMinutes.toString() }
  });
}


  constructor() { }
}

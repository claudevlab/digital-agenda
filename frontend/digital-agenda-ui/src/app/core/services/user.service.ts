import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProfessionalResponseDTO, UserResponseDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  // Base URL per tutti gli endpoint utente: http://localhost:8080/api/users
    private apiUrl = `${environment.apiUrl}/api/users`;

    constructor (private http : HttpClient) {}

    // GET /api/users/me
    // profilo dell'utente loggato
    getMyProfile() : Observable<UserResponseDTO> {
        return this.http.get<UserResponseDTO>(`${(this.apiUrl)}/me`);
    }

    // GET /api/users/professionals (con ricerca opzionale)
    // Se "search" e' vuoto o undefined . non aggiunge parametri e prende tutti
    // Lista professionisti visibile ai clienti
  getProfessionals(search?: string): Observable<ProfessionalResponseDTO[]> {
    let  params = new HttpParams()

    if (search && search.trim() != '') {
      params = params.set('search', search.trim());
    }
    return this.http.get<ProfessionalResponseDTO[]>(`${(this.apiUrl)}/professionals`, { params }); 
  }

  /*
APPUNTI PER ME : 
la struttura della chiamata httt.patch deve avere 3 componenti 
- url
- body ( in questo caso il payload che e' il body con in dati da inviare al backend)
- parametri opzionali es. devi inviare la risposta in testo
*/

  // per salvare il numero di tel con il login di google
    updatePhone(phoneNumber: string) {
    return this.http.patch(`${environment.apiUrl}/users/update-phone`, { phoneNumber });
  }

  updateProfessionalProfile(payload: any) {
    return this.http.patch(`${this.apiUrl}/me/professional-profile`,payload, {
      responseType: 'text'
    });

  }



}
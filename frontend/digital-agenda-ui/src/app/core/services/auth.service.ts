import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, User } from '../models/auth.model';
import { tap } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;

  // utilizziamo la libreria signal (per gestire l'utente connesso in modo piu' reattivo)
  // all'accesso segna l'user e lo cancella all'uscita
  currentUser = signal<User | null>(null); // user non e' quello del backend lo definisce il server

  constructor(private http: HttpClient) {
    this.checkInitialAuth();
  }

  // controlla gli accessi
  login(credentials: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(tap(response => {
      // response e' l'intero oeggetto in JSON quindi id,user, email.ecc
      // pipe-> prima di completare l'operazione fai questo
      // tap guarda ma senza modificarla
      // quando il login ha successo, salviamo l'utente
      localStorage.setItem('jwt_token', response.token);

      const user: User = {
        id: response.id,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        role: response.role,
        phoneNumber: response.phoneNumber
      };

      localStorage.setItem('user', JSON.stringify(user));
      this.currentUser.set(user);
    })
    );
  }

  // al logout cancella il token 
  logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
    this.currentUser.set(null);
  }

  // ottiene il token
  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  checkInitialAuth() {
    // recuper utente se ricarica la pagina
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      this.currentUser.set(JSON.parse(savedUser));
    }
  }

    register(payload: any) {
    const role = payload.role;
    let endpoint = '';

    // Facciamo UNA SOLA copia del payload per non modificare l'originale
    // e rimuoviamo subito il ruolo
    const { role: _, ...dataToSend } = payload; 

    if (role === 'CLIENT') {
      endpoint = `${this.apiUrl}/register/customer`;

      // Rimuoviamo i campi specifici del professionista dal payload
      delete dataToSend.jobTitle;
      delete dataToSend.vatRegistrationNumber;
      delete dataToSend.remote;
      delete dataToSend.onSite;
      
    } else if (role === 'PROFESSIONAL') {
      endpoint = `${this.apiUrl}/register/professional`;
    }
    
    // Facciamo la chiamata HTTP verso l'endpoint corretto
    return this.http.post(endpoint, dataToSend);
  }

  // RECUPERO PASSWORD
   // Richiede l'invio dell'email con il link
  forgotPassword (email : string) {
    // Nota: inviamo un oggetto JSON { email: "..." } come richiesto dal DTO backend
    return this.http.post(`${this.apiUrl}/forgot-password`, { email }, { responseType: 'text' });
  }

  // RESET PASSWORD
  // Invia la nuova passwored insieme al token
  resetPassword (token: string, newPassword: string) {
    return this.http.post(`${this.apiUrl}/reset-password`, { token, newPassword }, { responseType: 'text' });
  }

  // metodo per fare l'upgrade del professionista
  upgradeToProfessional(payload: any)  {
    const userApiUrl = `${environment.apiUrl}/users`;
    return this.http.patch(`${userApiUrl}/upgrade-to-professional`, payload, { responseType: 'text' });
  }

}

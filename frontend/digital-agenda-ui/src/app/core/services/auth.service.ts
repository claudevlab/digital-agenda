import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, User } from '../models/auth.model';
import { tap , catchError, Observable} from 'rxjs';
import { Router } from '@angular/router';

// Il servizio di autenticazione gestisce tutte le operazioni legate all'autenticazione, come login, logout, registrazione e gestione del token. Utilizza HttpClient per comunicare con il backend e signal per mantenere lo stato dell'utente connesso in modo reattivo.
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = `${environment.apiUrl}/api/auth`;

  // utilizziamo la libreria signal (per gestire l'utente connesso in modo piu' reattivo)
  // all'accesso segna l'user e lo cancella all'uscita
  currentUser = signal<User | null>(null); // user non e' quello del backend lo definisce il server
  router: any;

  // http client per fare le chiamate al backend. E il postino che consegna le richieste al server
  constructor(private http: HttpClient) {
    this.checkInitialAuth();
  }

  // controlla gli accessi
  login(credentials: LoginRequest) { 
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        
        //  SALVA TOKEN in localStorage -- da disabilitare se vuoi implementare httpOnly
        localStorage.setItem('jwt_token', response.token);
        
        const user: User = {
          id: response.id,
          email: response.email,
          firstName: response.firstName,
          lastName: response.lastName,
          role: response.role,
          phoneNumber: response.phoneNumber
        };
        
        localStorage.setItem('user', JSON.stringify(user));  // Solo dati user, NON token
        this.currentUser.set(user);
      })
    );
  }


  // al logout cancella il token 
 // LOGOUT: Backend cancella cookie automaticamente
  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user');  // Cancella solo dati user
        this.currentUser.set(null);
        this.router.navigate(['/login']);
      })
    );
  }
  


  // ottiene il token
  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

 checkInitialAuth() {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      this.currentUser.set(JSON.parse(savedUser));
    }
  }

  getCurrentUser(): User | null {
    return this.currentUser();
  }

    register(payload: any) {
    const role = payload.role;
    let endpoint = '';

    // Facciamo UNA SOLA copia del payload per non modificare l'originale escludendo il ruolo
    const { role: _, ...dataToSend } = payload; 

    // pattern comune - stesso from sue endpoint diversi a seconda del ruolo
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
    const userApiUrl = `${environment.apiUrl}/api/users`;
    return this.http.patch(`${userApiUrl}/upgrade-to-professional`, payload, { responseType: 'text' });
  }

}

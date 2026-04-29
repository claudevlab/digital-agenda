import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service'; 

@Component({
  selector: 'app-login-success',
  standalone: true,
  template: `
    <div class="d-flex justify-content-center align-items-center vh-100">
      <div class="text-center">
        <div class="spinner-border text-primary" role="status"></div>
        <h4 class="mt-3">Autenticazione in corso...</h4>
      </div>
    </div>
  `
})
export class LoginSuccessComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);
  private userService = inject(UserService); // Ti serve per chiamare l'endpoint che restituisce il profilo

  ngOnInit() {
    // 1. Legge il token dalla query string
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      
      if (token) {
        // 2. Salva il token nel localStorage
        localStorage.setItem('jwt_token', token);
        
        // 3. Poiché dal backend ci arriva SOLO il token, dobbiamo chiamare un endpoint 
        // per ottenere i dettagli dell'utente (email, ruolo, ecc.) per l'AuthService.
        
        this.userService.getMyProfile().subscribe({
          next: (userProfile) => {
            
            // Salviamo l'utente nello state dell'AuthService
            const userToSave = {
              id: userProfile.id,
              email: userProfile.email,
              firstName: userProfile.firstName,
              lastName: userProfile.lastName,
              role: userProfile.role,
              phoneNumber: userProfile.phoneNumber

            };
            
            localStorage.setItem('user', JSON.stringify(userToSave));
            this.authService.currentUser.set(userToSave);

            // 4. Reindirizzamento in base al ruolo
            if (userProfile.role === 'PROFESSIONAL') {
              this.router.navigate(['/dashboard']);
            } else {
              this.router.navigate(['/booking']);
            }
          },
          error: (err) => {
            console.error('Errore nel recupero profilo', err);
            this.router.navigate(['/login']);
          }
        });
      } else {
        // Niente token, torna al login
        this.router.navigate(['/login']);
      }
    });
  }
}
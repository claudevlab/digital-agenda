import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';
import { LoginRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule], // standard moderni
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  // servizi che usano la funzione inject() 
  // NEW nonNullable per evitare di dover gestire il caso null in tutto il componente
  private formBuilder = inject(FormBuilder).nonNullable;
  private authService = inject(AuthService);
  private router = inject(Router);

  googleLoginUrl = environment.apiUrl + '/oauth2/authorization/google';

  // Signal per gestire lo stato di caricamento del bottone
  isLoading = signal(false);

  // Signal per mostrare eventuali errori dal server
  errorMessage = signal('');

  // NEW : form tipizzato 
  loginForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  // getter per comodita' nell HTML
  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched(); // mostra gli errori senza compilare 
      return;
    }

    // sempre nel submit 
    this.isLoading.set(true);
    this.errorMessage.set('');


    // estraggo i dati validati
    const credentials : LoginRequest = {
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!
    };

    // chiamo l'API dal backend
    this.authService.login(credentials).subscribe({
      next: (res) => {
        this.isLoading.set(false);

        // Reindirizzamento in base al ruolo 
        if (res.role == 'PROFESSIONAL') {
          this.router.navigate(['/dashboard']);
        } else {
          this.router.navigate(['/booking']);
        }
      },

      error: (err) => {
  this.isLoading.set(false);
  
  // Distingue tipo di errore
  if (err.status === 401) {
    this.errorMessage.set('Credenziali non valide. Riprova');
  } else if (err.status === 500) {
    this.errorMessage.set('Errore server. Riprova più tardi');
  } else if (err.name === 'NetworkError') {
    this.errorMessage.set('Connessione internet assente');
  } else {
    this.errorMessage.set('Si è verificato un errore imprevisto');
  }

}
    });


  }
}

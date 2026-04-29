import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule], // standard moderni
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  // servizi che usano la funzine inject() 
  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService)
  private router = inject(Router);

  // Signal per gestire lo stato di caricamento del bottone
  isLoading = signal(false);

  // Signal per mostrare eventuali errori dal server
  errorMessage = signal('');

  // creazione del Reactive Form con validazioni
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
    const credentials = {
      email: this.loginForm.value.email as string,
      password: this.loginForm.value.password as string
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
        // gestione errore base
        this.errorMessage.set('Credenziali non valide. Riprova');
        console.error('Errore di login', err);
      }
    });


  }
}

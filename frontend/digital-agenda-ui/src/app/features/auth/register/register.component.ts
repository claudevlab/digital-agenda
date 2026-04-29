import { Component, inject, signal } from '@angular/core';
import { FormBuilder,ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router , RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // utilizziamo signal per gestire lo stato : 'CLIENT' | 'PROFESSIONAL'
  selectedRole = signal <'CLIENT' | 'PROFESSIONAL'> ('CLIENT');

  isLoading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  // creiamo il form con tutti i campo possibili
  // faremo in modo che quelli specifici del professionista non saranno validati se il ruolo e CLIENT
  registerForm = this.formBuilder.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
    // campi esclusivi per PROFESSIONAL
    jobTitle: [''],
    vatRegistrationNumber: [''],
    remote: [false],
    onSite: [false],
  });

  get f() {
    return this.registerForm.controls;
  }

  // metodo per switchare i tab e aggiornare le validazioni dinamicamente
  switchRole(role : 'CLIENT' | 'PROFESSIONAL') {
    this.selectedRole.set(role);
    this.registerForm.reset(); // pulisce al cambio tab

    const jobTitleControl = this.registerForm.get('jobTitle');
    const vatControl = this.registerForm.get('vatRegistrationNumber');

    if (role === 'PROFESSIONAL') {
      // se e' un professinista i seguenti campi diventano obbligatori
      jobTitleControl?.setValidators([Validators.required]);
      vatControl?.setValidators([Validators.required]);
    } else {
       // Se è cliente, rimuoviamo le validazioni
      jobTitleControl?.clearValidators();
      vatControl?.clearValidators();
    }

     // Aggiorniamo lo stato dei controlli
    jobTitleControl?.updateValueAndValidity();
    vatControl?.updateValueAndValidity();
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
  }

  this.isLoading.set(true);
  this.errorMessage.set('');

  const formValue = this.registerForm.value;
  // aggiungiamo il ruolo selezionato al payload
  const payload = {...formValue, role: this.selectedRole() };

  this.authService.register(payload).subscribe({
    next: (res) => {
      this.isLoading.set(false);
      this.successMessage.set('Registrazione avvenuta con successo! A breve riceverai l\'email di conferma.');
      // dopo 3 secondi rimanda al login
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 3000);
    },
    error: (err) => {
      this.isLoading.set(false);
      this.errorMessage.set('Si è verificato un errore durante la registrazione. Controlla i dati e riprova');
      console.error('Errore di registrazione', err);
    }
  });

  }


}
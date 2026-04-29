import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-upgrade',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './upgrade.component.html'
})
export class UpgradeComponent {
  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  isLoading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  upgradeForm = this.formBuilder.group({
    jobTitle: ['', Validators.required],
    vatRegistrationNumber: ['', [ Validators.minLength(11), Validators.maxLength(11)]],
    phoneNumber: ['', Validators.required],
    remote: [false],
    onSite: [false]
  });

  get f() { return this.upgradeForm.controls; }

  onSubmit() {
    if (this.upgradeForm.invalid) {
      this.upgradeForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const payload = this.upgradeForm.value;

    this.authService.upgradeToProfessional(payload).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.successMessage.set(res || 'Upgrade completato con successo!');
        
        // Dopo 3 secondi facciamo il logout forzato e rimandiamo al login
        // in modo che l'utente prenda il nuovo token con il ruolo aggiornato.
        setTimeout(() => {
          this.authService.logout();
          this.router.navigate(['/login']);
        }, 4000);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error || 'Errore durante l\'aggiornamento.');
      }
    });
  }
}
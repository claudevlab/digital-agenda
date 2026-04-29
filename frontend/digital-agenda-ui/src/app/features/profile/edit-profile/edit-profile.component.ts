import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,RouterModule],
  templateUrl: './edit-profile.component.html'
})
export class EditProfileComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  

  profileForm = this.formBuilder.group({
    jobTitle: ['', Validators.required],
    vatRegistrationNumber: [''],
    phoneNumber: ['', Validators.required],
    remote: [false],
    onSite: [false]
  });

  ngOnInit() {
    // Quando la pagina si carica, andiamo a prendere i dati aggiornati dal DB
    this.userService.getMyProfile().subscribe({
      next: (userData) => {
        // patchValue è la best practice per pre-compilare un form reattivo
        // popola solo i campi che trovano corrispondenza
        this.profileForm.patchValue({
          jobTitle: userData.jobTitle || '',
          phoneNumber: userData.phoneNumber || '',
          remote: userData.remote || false,
          onSite: userData.onSite || false
          // (se hai aggiunto vatRegistrationNumber nel DTO /me aggiungilo qui)
        });
      }
    });
  }

  onSubmit() {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    this.userService.updateProfessionalProfile(this.profileForm.value).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.successMessage.set('Profilo aggiornato con successo! Ritorno alla dashboard');
        
        // Aggiorniamo anche l'utente in locale
        const current = this.authService.currentUser();
        if (current) {
          const updated = { ...current, ...this.profileForm.value };
          this.authService.currentUser.set(updated as any);
          localStorage.setItem('user', JSON.stringify(updated));
        }

        // redirect alla dashboard dopo 1.5 secondi , il tempo di visualizzare la scritta  dell'avvenuta modifica dei dati
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1500);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error || 'Errore durante il salvataggio.');
      }
    });
  }
}
import { CommonModule } from '@angular/common';
import { Component, inject , signal } from '@angular/core';
import { ReactiveFormsModule , Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
private formBuilder = inject(FormBuilder);
private authService = inject(AuthService);

 isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  forgotForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]]
  });

  get f() { return this.forgotForm.controls; }

  onSubmit() {
    if (this.forgotForm.invalid) {
      this.forgotForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const email = this.forgotForm.value.email as string;

    this.authService.forgotPassword(email).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        // Il messaggio arriverà dal backend
        this.successMessage.set(res || 'Ti abbiamo inviato un\'email con le istruzioni.');
        this.forgotForm.reset();
      },
      error: (err) => {
        this.isLoading.set(false);
        // Poiché riceviamo testo puro, l'errore è spesso in err.error
        this.errorMessage.set(err.error || 'Si è verificato un errore. Riprova più tardi.');
      }
    });
  }


}



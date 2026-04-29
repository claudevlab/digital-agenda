import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {

  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  token = signal<string | null>(null);
  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  resetForm = this.formBuilder.group({
    newPassword: ['', [Validators.required, Validators.minLength(8)]]
  });

  get f() { return this.resetForm.controls; }

  ngOnInit() {
    // Leggiamo il token dall'URL (es: /reset-password?token=xxx)
    this.route.queryParams.subscribe(params => {
      const tokenParam = params['token'];
      if (tokenParam) {
        this.token.set(tokenParam);
      } else {
        this.errorMessage.set("Link non valido o token mancante.");
      }
    });
  }

  onSubmit() {
    if (this.resetForm.invalid || !this.token()) {
      this.resetForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const newPass = this.resetForm.value.newPassword as string;

    this.authService.resetPassword(this.token()!, newPass).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.successMessage.set(res || 'Password modificata con successo!');
        
        // Rimanda al login dopo 3 secondi
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error || 'Errore durante il cambio password. Il link potrebbe essere scaduto.');
      }
    });
  }

}

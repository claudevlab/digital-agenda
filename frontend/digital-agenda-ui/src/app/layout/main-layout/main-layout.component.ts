import { CommonModule } from '@angular/common';
import { Component, inject,signal } from '@angular/core';
import { Router, RouterLinkActive, RouterOutlet ,RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule,RouterOutlet,RouterLinkActive,RouterLink, FormsModule], // import per il router interno
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {

  private authService = inject(AuthService);
  private userService = inject(UserService);
  private router = inject(Router);

  // continuo ad utilizzare signal dell'utente corrente ( + reattivo)
  user = this.authService.currentUser;

  // Variabili per il Modal del numero di telefono
  missingPhone = signal('');
  isSaving = signal(false);
  errorMessage = signal('');

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Metodo per salvare il telefono obbligatorio
  savePhone() {
    if (!this.missingPhone() || this.missingPhone().trim().length < 8) {
      this.errorMessage.set('Inserisci un numero di telefono valido (min. 8 caratteri).');
      return;
    }

    this.isSaving.set(true);
    this.errorMessage.set('');

    // Chiama il backend per fare l'update (PATCH /api/users/update-phone)
    this.userService.updatePhone(this.missingPhone()).subscribe({
      next: () => {
        this.isSaving.set(false);
        
        // Aggiorniamo l'utente locale così il modal sparisce all'istante
        const currentUser = this.authService.currentUser();
        if (currentUser) {
          const updatedUser = { ...currentUser, phoneNumber: this.missingPhone() };
          localStorage.setItem('user', JSON.stringify(updatedUser));
          this.authService.currentUser.set(updatedUser);
        }
      },
      error: () => {
        this.isSaving.set(false);
        this.errorMessage.set('Errore durante il salvataggio. Riprova.');
      }
    });
  }

}

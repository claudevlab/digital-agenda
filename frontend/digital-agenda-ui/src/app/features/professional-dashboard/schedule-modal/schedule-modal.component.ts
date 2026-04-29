import { CommonModule } from '@angular/common';
import { Component, inject, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ScheduleService } from '../../../core/services/schedule.service';
import { AuthService } from '../../../core/services/auth.service';
import { ScheduleRequest, DayOfWeek } from '../../../core/models/schedule.model';


@Component({
  selector: 'app-schedule-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './schedule-modal.component.html',
  styleUrl: './schedule-modal.component.scss'
})
export class ScheduleModalComponent {

  private formBuilder = inject(FormBuilder);
  private scheduleService = inject(ScheduleService);
  private authService = inject(AuthService);

  // Output moderni di Angular 17+
  scheduleCreated = output<void>();
  closeModal = output<void>();

  isLoading = signal(false);
  errorMessage = signal('');

  // Lista giorni: value = quello che va al backend, label = quello che vede l'utente
  days: { value: DayOfWeek; label: string }[] = [
    { value: 'MONDAY', label: 'Lunedì' },
    { value: 'TUESDAY', label: 'Martedì' },
    { value: 'WEDNESDAY', label: 'Mercoledì' },
    { value: 'THURSDAY', label: 'Giovedì' },
    { value: 'FRIDAY', label: 'Venerdì' },
    { value: 'SATURDAY', label: 'Sabato' },
    { value: 'SUNDAY', label: 'Domenica' },
  ];

  scheduleForm = this.formBuilder.group({
    dayOfWeek: ['', Validators.required],
    startTime: ['', Validators.required],
    endTime: ['', Validators.required],
    description: ['']
  });

  get f() { return this.scheduleForm.controls; }

  onSubmit() {
    if (this.scheduleForm.invalid) {
      this.scheduleForm.markAllAsTouched(); // quando clicchi sullo spazio di inserimento testo ti da i suggerimenti
      return;
    }

    const user = this.authService.currentUser();
    if (!user?.id) {
      this.errorMessage.set('Utente non trovato. Rieffettua il login.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const request: ScheduleRequest = {
      dayOfWeek: this.scheduleForm.value.dayOfWeek as DayOfWeek,
      startTime: this.scheduleForm.value.startTime as string,
      endTime: this.scheduleForm.value.endTime as string,
      description: this.scheduleForm.value.description || undefined
    };

    this.scheduleService.createSchedule(user.id, request).subscribe({
      next: () => {
      this.isLoading.set(false);
      this.scheduleCreated.emit(); // avvisa la classe padre che lo slot e' stato creato
    },
      error: (err) => {
        console.error('Errore creazione schedule:', err);
        this.errorMessage.set('Impossibile creare lo slot. Riprova.');
        this.isLoading.set(false);
      }
    });
  }

      onClose() {
    this.closeModal.emit(); // avvisa la classe padre: chiudi il modale!
  }


      


  }



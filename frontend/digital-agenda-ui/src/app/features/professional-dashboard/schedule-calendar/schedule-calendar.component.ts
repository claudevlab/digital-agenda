import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import itLocale from '@fullcalendar/core/locales/it';
import { AuthService } from '../../../core/services/auth.service';
import { ScheduleExceptionService } from '../../../core/services/schedule-exception.service';
import { ScheduleExceptionResponse } from '../../../core/models/schedule-exception.model';

@Component({
  selector: 'app-schedule-calendar',
  standalone: true,
  imports: [CommonModule, FullCalendarModule],
  templateUrl: './schedule-calendar.component.html',
  styleUrls: ['./schedule-calendar.component.scss']
})
export class ScheduleCalendarComponent implements OnInit {
  private authService = inject(AuthService);
  private exceptionService = inject(ScheduleExceptionService);

  isLoading = signal(true);
  errorMessage = signal('');
  exceptions = signal<ScheduleExceptionResponse[]>([]);

  calendarOptions = signal<CalendarOptions>({
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    locale: itLocale,
    selectable: true,
    weekends: true,
    height: 'auto',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth'
    },
    dateClick: (info) => this.onDateClick(info.dateStr),
    eventClick: (info) => this.onEventClick(info),
    events: []
  });

  ngOnInit() {
    const user = this.authService.currentUser();
    if (user?.id) {
      this.loadExceptions(user.id);
    } else {
      this.isLoading.set(false);
    }
  }

  loadExceptions(professionalId: number) {
    this.isLoading.set(true);
    this.exceptionService.getExceptionsByProfessional(professionalId).subscribe({
      next: (data) => {
        this.exceptions.set(data);
        this.updateCalendarEvents(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Errore caricamento eccezioni:', err);
        this.errorMessage.set('Impossibile caricare il calendario.');
        this.isLoading.set(false);
      }
    });
  }

  // Converte le eccezioni in eventi rossi per FullCalendar
  private updateCalendarEvents(data: ScheduleExceptionResponse[]) {
    this.calendarOptions.update(options => ({
      ...options,
      events: data.map(ex => ({
        id: String(ex.id),
        title: ex.reason || 'Non disponibile',
        date: ex.date,
        backgroundColor: '#ef4444',
        borderColor: '#dc2626',
        textColor: '#ffffff'
      }))
    }));
  }

  onDateClick(dateStr: string) {
    const user = this.authService.currentUser();
    if (!user?.id) return;

    // Controlla se il giorno è già bloccato
    const existing = this.exceptions().find(ex => ex.date === dateStr);

    if (existing) {
      // Giorno già bloccato -> chiede conferma per sbloccarlo
      const conferma = window.confirm(
        `Il giorno ${dateStr} è già bloccato.\nVuoi renderlo di nuovo disponibile?`
      );
      if (conferma) {
        this.removeException(existing.id);
      }
    } else {
      // Giorno libero -> chiede motivo e lo blocca
      const reason = window.prompt(
        `Vuoi bloccare il giorno ${dateStr}?\nMotivo (opzionale):`,
        ''
      );
      if (reason === null) return; // utente ha premuto Annulla
      this.addException(dateStr, reason, user.id);
    }
  }

  onEventClick(info: any) {
    const user = this.authService.currentUser();
    if (!user?.id) return;

    const conferma = window.confirm(
      `Vuoi sbloccare il giorno ${info.event.startStr}?`
    );
    if (conferma) {
      this.removeException(Number(info.event.id));
    }
  }

  private addException(date: string, reason: string, professionalId: number) {
    this.exceptionService.createException(professionalId, {
      date,
      reason: reason || undefined
    }).subscribe({
      next: (newEx : ScheduleExceptionResponse) => {
        // Aggiorna la lista locale aggiungendo la nuova eccezione
        this.exceptions.update(list => [...list, newEx]);
        // Aggiunge l'evento rosso al calendario
        this.calendarOptions.update(options => ({
          ...options,
          events: [
            ...(options.events as any[]),
            {
              id: String(newEx.id),
              title: newEx.reason || 'Non disponibile',
              date: newEx.date,
              backgroundColor: '#ef4444',
              borderColor: '#dc2626',
              textColor: '#ffffff'
            }
          ]
        }));
      },
      error: (err) => {
        console.error('Errore blocco giorno:', err);
        this.errorMessage.set('Impossibile bloccare il giorno. Riprova.');
      }
    });
  }

  private removeException(id: number) {
    this.exceptionService.deleteException(id).subscribe({
      next: () => {
        // Rimuove dalla lista locale
        this.exceptions.update(list => list.filter(ex => ex.id !== id));
        // Rimuove l'evento rosso dal calendario
        this.calendarOptions.update(options => ({
          ...options,
          events: (options.events as any[]).filter(ev => ev.id !== String(id))
        }));
      },
      error: (err) => {
        console.error('Errore sblocco giorno:', err);
        this.errorMessage.set('Impossibile sbloccare il giorno. Riprova.');
      }
    });
  }
}

import { CommonModule } from '@angular/common';
import { Component, OnInit} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProfessionalResponseDTO } from '../../../core/models/user.model';
import { AvailableSlotDTO } from '../../../core/models/schedule.model';
import { AppointmentRequestDTO, AppointmentResponseDTO } from '../../../core/models/appointment.model';
import { ScheduleService } from '../../../core/services/schedule.service';
import { AppointmentService } from '../../../core/services/appointment.service';
import { UserService } from '../../../core/services/user.service';
import { ConfirmModalComponent } from '../../../shared/components/confirm-modal/confirm-modal.component';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, FormsModule,ConfirmModalComponent,RouterLink],
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.scss'
})
export class BookingComponent implements OnInit {

  // EDIT  Controlla se mostrare il form di prenotazione o la dashboard
showBookingForm: boolean = false;

  // il booking e suddiviso in 4 step lineari ( l'utente non puo' saltarli)
  
  // 1 - scelta del professionista
  professionals : ProfessionalResponseDTO[] = [];
  selectedProfessional: ProfessionalResponseDTO | null = null;  

  // 2 - scelta data e slot disponibili
  selectedDate: string = '';
  selectedDateTime : string = ''; // ISO per il backend: "YYYY-MM-DDTHH:mm:ss";

  /*
   BUG FIXED : 
   Il bug è nel metodo selectSlot — quando cambi la durata con il (change)="selectSlot(slot)" nel select, viene chiamato selectSlot solo per l'ultimo slot iterato dal *ngFor, sovrascrivendo timeSlots per entrambi.
  */
  timeSlotsMap: Map<number,string[]> = new Map();        // slot orari generati
  selectedTimeSlot: string = '';   // slot scelto dal cliente es. "10:30 - 11:30"


  // 3 : conferma 
  durationMinutes: number = 60; // defautl 60
  notes: string = '';

  // stato UI 
  step: number = 1;
  loading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  //appuntamenti gia' prenotati dal cliente
  myAppointments : AppointmentResponseDTO[] = [];

  // Data minima selezionabile = oggi
  today: string = new Date().toISOString().split('T')[0];

  // Modal di conferma cancellazione
showCancelModal: boolean = false;
appointmentToCancel: number | null = null;

  // variabili per la ricerca 
searchQuery : string = '';
private searchSubject: Subject<string> = new Subject<string>();

  constructor(
    private userService: UserService,
    private scheduleService: ScheduleService,
    private appointmentService: AppointmentService
  ) {}

  ngOnInit() : void {
    this.loadProfessionals();
    this.loadMyAppointments();

    // CONFIGURAZIONE DELLA RICERCA REATTIVA
    // -- useremo Subject di RxJS. Questo intercetta quello che l'utente scrive e aspetta 300 millisecondi di inattività prima di fare la chiamata al backend , evitando chiamate inutili.
  this.searchSubject.pipe(
      debounceTime(300), // Aspetta 300ms prima di procedere
      distinctUntilChanged() // Procedi solo se il valore è effettivamente cambiato
    ).subscribe((query) => {
      this.loadProfessionals(query);
    });
  }

  // --- CARICAMENTO DATI ----

  loadProfessionals(search?: string): void {
    this.loading = true; // Mostriamo un feedback visivo durante il caricamento
    this.errorMessage = '';
    this.userService.getProfessionals(search).subscribe({
      next: (data: ProfessionalResponseDTO[]) => {
        this.professionals = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Impossibile caricare i professionisti';
        this.loading = false;
      }
    });
  }

  // Buona pratica: pulire i subject quando il componente viene distrutto
  ngOnDestroy(): void {
    this.searchSubject.complete();
  }

  // ricerca : Chiamata dall'HTML ogni volta che si digita
  onSearchChange(query: string): void {
    this.searchSubject.next(query);
  }

  // --- STEP 1 : SCEGLI IL PROFESSIONISTA
  selectProfessional(prof: ProfessionalResponseDTO) : void {
    this.selectedProfessional = prof;
    this.selectedDate = '';
    this.timeSlotsMap.clear();
    this.selectedTimeSlot = '';
    this.step = 2;
  }

  // --- STEP 2 : SCEGLI IL PROFESSIONISTA
  onDateChange(): void {
    if (!this.selectedDate || !this.selectedProfessional) return;
    
    this.loading = true;
    this.timeSlotsMap.clear();
    this.selectedTimeSlot = '';

    // utilizziamo il nuovo endPoint che filtra i slot gia' occupati
    this.scheduleService.getAvailableSlots(
      this.selectedProfessional.id ,
      this.selectedDate,
      this.durationMinutes
    ).subscribe ({
        next: (slots: AvailableSlotDTO[]) => {
      // Salviamo nella mappa solo gli slot disponibili
      // key 0 = unico gruppo
        const freeSlots = slots
        .filter(s => s.available)
        .map(s => `${s.startTime} - ${s.endTime}`);
      
        this.timeSlotsMap.set(0,freeSlots);
        this.loading = false;
      
    },
    error: () => {
      this.errorMessage = 'Errore nel caricamento degli slot.';
      this.loading = false;
    } 
  });

}

    // Converte "YYYY-MM-DD" → "MONDAY", "TUESDAY" ecc.
  getDayOfWeek(dateStr: string): string {
    const days = ['SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'];
    const date = new Date(dateStr);
    return days[date.getDay()];
  }

  // HANDLER
  getTimeSlotsForSchedule(key: number): string[] {
    return this.timeSlotsMap.get(key) || [];
  }

  getDayOfWeekItalian(dateStr: string): string {
  const days = ['Domenica','Lunedì','Martedì','Mercoledì','Giovedì','Venerdì','Sabato'];
  return days[new Date(dateStr).getDay()];
}

selectTimeSlot(timeSlot: string): void {
  this.selectedTimeSlot = timeSlot;
  // Costruiamo il LocalDateTime con l'orario esatto scelto
  const startTime = timeSlot.split(' - ')[0]; // "10:30"
  this.selectedDateTime = `${this.selectedDate}T${startTime}:00`;
  this.step = 3;
}


  // --- STEP 3: conferma ---

  confirmBooking(): void {
    if (!this.selectedProfessional || !this.selectedDate || !this.selectedTimeSlot) return;

    this.loading = true;
    this.errorMessage = '';

    const request: AppointmentRequestDTO = {
      professionalId: this.selectedProfessional.id,
      appointmentDateTime: this.selectedDateTime,
      durationMinutes: this.durationMinutes,
      notes: this.notes || undefined
    };

    this.appointmentService.bookAppointment(request).subscribe({
      next: () => {
        this.successMessage = 'Appuntamento prenotato con successo!';
        this.loading = false;
        this.step = 4;
        this.loadMyAppointments();
      },
      error: (err: any) => {
        this.errorMessage = err.error?.message || 'Errore durante la prenotazione.';
        this.loading = false;
      }
    });
  }

  // APPUNTAMENTI

   loadMyAppointments(): void {
    this.appointmentService.getCustomerAppointments().subscribe({
      next: (data) => this.myAppointments = data,
      error: () => {}
    });
  }

  // --- CANCELLA APPUNTAMENTO ---

 // Apre il modal invece del confirm() nativo
openCancelModal(id: number): void {
  this.appointmentToCancel = id;
  this.showCancelModal = true;
}

// Chiamato dal modal quando l'utente conferma
onCancelConfirmed(): void {
  if (!this.appointmentToCancel) return;

  this.appointmentService.cancelAppointment(this.appointmentToCancel).subscribe({
    next: () => {
      this.showCancelModal = false;
      this.appointmentToCancel = null;
      this.loadMyAppointments();
    },
    error: () => {
      this.errorMessage = 'Impossibile cancellare l\'appuntamento.';
      this.showCancelModal = false;
    }
  });
}

// Chiamato dal modal quando l'utente annulla
onCancelDismissed(): void {
  this.showCancelModal = false;
  this.appointmentToCancel = null;
}

  // --- RESET ---

  newBooking(): void {
  this.timeSlotsMap.clear();
  this.selectedTimeSlot = '';
  this.step = 1;
  this.selectedProfessional = null;
  this.selectedDate = '';
  this.notes = '';
  this.durationMinutes = 60;
  this.successMessage = '';
  this.errorMessage = '';
  this.showBookingForm = true;   // ✅ mostra il wizard
  this.activeTab = 'upcoming';

}

backToDashboard(): void {
  this.showBookingForm = false;
  this.step = 1;
  this.loadMyAppointments();
}

  // --- HELPERS ---

  getFullName(firstName: string, lastName: string): string {
    return `${firstName} ${lastName}`;
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'In attesa',
      CONFIRMED: 'Confermato',
      CANCELLED: 'Cancellato'
    };
    return map[status] || status;
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge bg-warning text-dark',
      CONFIRMED: 'badge bg-success',
      CANCELLED: 'badge bg-danger'
    };
    return map[status] || 'badge bg-secondary';
  }

  // Formatta "2026-03-09T10:00:00" → "09/03/2026 alle 10:00"
  formatDateTime(dateTime: string): string {
    if (!dateTime) return '—';
    const d = new Date(dateTime);
    return d.toLocaleDateString('it-IT') + ' alle ' + d.toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' });
  }

  // ---PROPIETA' CALCOLATE---
  get upcomingAppointments() : AppointmentResponseDTO[] {
    const now = new Date();
    return this.myAppointments.filter (
      apt => new Date(apt.appointmentDate) >= now && apt.appointmentStatus != 'CANCELLED' );
  }

  get pastAppointments() : AppointmentResponseDTO[] {
    const now = new Date();

    return this.myAppointments.filter (
      apt => new Date( apt.appointmentDate) < now || apt.appointmentStatus == 'CANCELLED'
    );
  }

  activeTab: 'upcoming' | 'past' = 'upcoming'; // di default mostra i prossimi

}



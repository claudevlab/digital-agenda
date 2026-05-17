import { CommonModule } from '@angular/common';
import { Component, inject, signal, OnInit } from '@angular/core';
import { ScheduleService } from '../../../core/services/schedule.service';
import { AuthService } from '../../../core/services/auth.service';
import { Schedule } from '../../../core/models/schedule.model';
import { computed } from '@angular/core';
import { ScheduleModalComponent } from '../schedule-modal/schedule-modal.component';
import { ScheduleCalendarComponent } from '../schedule-calendar/schedule-calendar.component';
import { AppointmentService } from '../../../core/services/appointment.service';
import { AppointmentResponseDTO } from '../../../core/models/appointment.model';
import { RouterModule } from '@angular/router';



@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ScheduleModalComponent, ScheduleCalendarComponent,RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {

  // injection dei services
  private scheduleService = inject(ScheduleService);
  private authService = inject(AuthService);
  private appointmentService = inject(AppointmentService);

  // utilizzo di signal (vedi doc)

  // lista degli schedule ricevuti dal backend
  schedules = signal<Schedule[]>([]);
  // true in attesa di risposta dal backend
  isLoading = signal(true);
  // messaggio di errore da mostrare all'utente
  errorMessage = signal('');
  // pulsante crea nuovo slot
  isModalOpen = signal(false); //controlla apertura e chiusura modale

  // reasonRejected 
  showRejectModal = signal(false);
  appointmentToReject = signal<number | null>(null);
  rejectReason = signal('');

  //Signal per il modale Info Cliente (visualizza i dettagli del cliente)
  showCustomerModal = signal(false);
  selectedCustomer = signal<{ name: string; email?: string; phone?: string } | null>(null);

  // facciamo in modo che si ricarica automaticamente ogni volta che cambia lo schedule() 
  // Angular lo aggiorna da solo ( per approfonsimenti vai a vedere la docs)
  totalSchedules = computed(() => this.schedules().length);

  // --- APPUNTAMENTI ---
  // contenitore di " appuntamenti"
  appointments = signal<AppointmentResponseDTO[]>([]);

  isLoadingAppointments = signal(false);

  // segnalazione errori
  appointmentError = signal('');

  // contatore appuntamenti pending
  pendingCount = computed(() => this.appointments().filter(a => a.appointmentStatus === 'PENDING').length);

  // contatore appuntamenti confirmed
  confirmedCount = computed(() => this.appointments().filter(a => a.appointmentStatus === 'CONFIRMED').length);

  // --- STORICI---
showHistorical = signal(false);               // toggle sezione storico
historicalAppointments = signal<AppointmentResponseDTO[]>([]); // lista accumulata
isLoadingHistorical = signal(false);
historicalError = signal('');
historicalCurrentPage = signal(0);
historicalHasMore = signal(true);             // false quando siamo all'ultima pagina
historicalTotalElements = signal(0);

readonly HISTORICAL_PAGE_SIZE = 10; 

// Apre la sezione e carica la prima pagina (solo al primo click)
toggleHistorical(): void {
  const isOpen = this.showHistorical();
  this.showHistorical.set(!isOpen);

  // Carica solo se stiamo aprendo E non abbiamo già dati
  if (!isOpen && this.historicalAppointments().length === 0) {
    this.loadHistoricalPage(0);
  }
}

// Carica una pagina specifica e ACCODA i risultati (lazy append)
loadHistoricalPage(page: number): void {
  if (this.isLoadingHistorical()) return; // evita doppia chiamata

  this.isLoadingHistorical.set(true);
  this.historicalError.set('');

  this.appointmentService.getHistoricalAppointments(page, this.HISTORICAL_PAGE_SIZE)
    .subscribe({
      next: (response) => {
        // Accoda i nuovi risultati a quelli già presenti
        this.historicalAppointments.update(list => [...list, ...response.content]);
        this.historicalCurrentPage.set(response.number);
        this.historicalHasMore.set(!response.last);
        this.historicalTotalElements.set(response.totalElements);
        this.isLoadingHistorical.set(false);
      },
      error: () => {
        this.historicalError.set('Impossibile caricare gli appuntamenti storici.');
        this.isLoadingHistorical.set(false);
      }
    });
}

// Chiamato dal bottone "Carica altri"
loadMoreHistorical(): void {
  if (this.historicalHasMore()) {
    this.loadHistoricalPage(this.historicalCurrentPage() + 1);
  }
}



  // mappatura dei giorni
  dayLabels: Record<string, string> = {
    'MONDAY': 'Lunedì',
    'TUESDAY': 'Martedì',
    'WEDNESDAY': 'Mercoledì',
    'THURSDAY': 'Giovedì',
    'FRIDAY': 'Venerdì',
    'SATURDAY': 'Sabato',
    'SUNDAY': 'Domenica'
  };

// ordina gli appuntamenti per data in modo ascendente
private sortAppointmentsByDateAsc(list: AppointmentResponseDTO[]): AppointmentResponseDTO[] {
return [...list].sort((a, b) =>
new Date(a.appointmentDate as unknown as string).getTime() -
new Date(b.appointmentDate as unknown as string).getTime()
);
}

  // viene chiamato quando il compoiennte e' stato creato
  ngOnInit() {
    console.log('=== ngOnInit dashboard ===');
    const user = this.authService.currentUser();
    console.log('User:', user);

    if (user?.id) {
      this.loadSchedules(user.id);
    } else {
      this.errorMessage.set('Utente non trovato. Rieffettua il login');
      this.isLoading.set(false);
    }

    this.loadAppointments();

      console.log('Chiamo loadAppointments...');
  }

  // metodo che carica la lista degli SCHEDULE dal backend
  loadSchedules(id: number) {
    this.isLoading.set(true);

    this.scheduleService.getScheduleByProfessional(id).subscribe({
      next: (data) => {
        //aggiorniamo il signal con i dati ricevuti
        this.schedules.set(data);
        this.isLoading.set(false);
      },

      error: (err) => {
        console.error('Errore caricamento schedule', err);
        this.errorMessage.set('Impossibile caricare gli schedule. Ripova');
        this.isLoading.set(false);
      }
    });
  }

  // Elimina uno schedule per id
  // Chiamato dal bottone "Elimina" nell'HTML
  deleteSchedule(id: number) {

    // LOG TEMPORANEO per debug
    console.log('Token presente:', this.authService.getToken());
    console.log('Chiamata DELETE per id:', id);

    this.scheduleService.deleteSchedule(id).subscribe({
      next: () => {
        // invece di ricaricare tutto dal server,
        //aggiorniamo il signal filtrando via lo schedule eliminato
        // piu veloce perche non esegue una seconda chiamata HTTP
        this.schedules.update(list => list.filter(s => s.id != id));
      },
      error: (err) => {
        console.error('Errore eliminazione schedule: ', err);
        this.errorMessage.set('Impossibile eliminare lo schedule. Rirpova');
      },
    });
  }

  openModal() {
    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
  }

  onScheduleCreated() {
    this.closeModal();

    // ricarica la lsita dal backend per mostrare un nuovo slot
    const user = this.authService.currentUser();
    if (user?.id) {
      this.loadSchedules(user.id);

    }
  }

  // --- APPUNTAMENTI ---
  loadAppointments() {
    console.log('=== loadAppointments chiamato ===');

    this.isLoadingAppointments.set(true); // caricamento
    this.appointmentService.getProfessionalAppointments().subscribe({ // lista di tutti gli appuntamenti

    next: (data: AppointmentResponseDTO[]) => {
        console.log('Appuntamenti ricevuti:', data);
        const sorted = this.sortAppointmentsByDateAsc(data);
      this.appointments.set(sorted);
      this.isLoadingAppointments.set(false);
    },
    error: (err) => {
        console.error('Errore appuntamenti:', err);
      this.appointmentError.set('Impossibile caricare gli appuntamenti.');
      this.isLoadingAppointments.set(false);
    }
    
  });

}

confirmAppointment (id : number) {
  this.appointmentService.updateAppointmentStatus(id,'CONFIRMED').subscribe ({
    next: (updated : AppointmentResponseDTO) => {
      this.appointments.update ( list =>   // non modifico tutto come fa il set ma con list modifico solo una cosa dentro
        list.map(a => a.id == id ? updated : a) 
      );
    },
    error: () => this.appointmentError.set('Errore nella conferma')
  });
}

openRejectModal(id : number) {
this.appointmentToReject.set(id);
this.rejectReason.set('');
this.showRejectModal.set(true);
}

closeRejectModal() {
  this.showRejectModal.set(false);
  this.appointmentToReject.set(null);
  this.rejectReason.set('');
}

confirmReject() {
  const id = this.appointmentToReject();
 if (!id) return ;

  const reason = this.rejectReason();
  if (!reason) return;

  this.appointmentService.updateAppointmentStatus(id, 'REJECTED', reason).subscribe({
    next: (updated: AppointmentResponseDTO) => {
      // aggiorniamo la lista nel signal
      this.appointments.update(list => list.map(a => a.id == id ? updated : a));
      this.closeRejectModal();
      this.appointments.set(this.appointments().filter(a => a.id != id)); // rimuove l'appuntamento rifiutato dalla lista
    },
      error: () => {
        this.appointmentError.set('Errore nel rifiuto appuntamento');
        this.closeRejectModal();
      }
    });
  }


// converte l'orario in ita
formatDateTime(dateTime: string): string {
    if (!dateTime) return 'errore : nessuna data disponibile';
    const d = new Date(dateTime);
    return d.toLocaleDateString('it-IT') + ' alle ' +
           d.toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' }); // utilizza die cifre
  }

  // --- TRADUTTORE DI ETICHETTE ---
  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'In attesa',
      CONFIRMED: 'Confermato',
      REJECTED: 'Rifiutato'
    };
    return map[status] || status;
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge-pending',
      CONFIRMED: 'badge-confirmed',
      REJECTED: 'badge-rejected',
    };
    return map[status] || '';
  }

  // MIGLIORAMENTO ELENCO APPUNTAMENTI 
  // rendiamo le schede "in attesa" e "confermati" cliccabili con seguente lista 
  // aggiungiamo anche la possibilita' di filtrare gli appuntamenti in base al loro stato

  // Filtro attivo: null = tutti, 'PENDING' = in attesa, 'CONFIRMED' = confermati
  activeFilter: 'PENDING' | 'CONFIRMED' | null = null;

  // esclude gli appuntamenti passati dalla lista principale
 get filteredAppointments(): AppointmentResponseDTO[] {
  const today = new Date();
  today.setHours(0, 0, 0, 0); // mezzanotte

  const base = this.appointments().filter(a => {
    const aptDate = new Date(a.appointmentDate as unknown as string);
    return aptDate >= today; // esclude tutto ciò che è prima di oggi
  });

  const filtered = !this.activeFilter
    ? base
    : base.filter(a => a.appointmentStatus === this.activeFilter);

  return this.sortAppointmentsByDateAsc(filtered);
}

    // Toggle: click sulla stessa card deseleziona il filtro
    setFilter(filter: 'PENDING' | 'CONFIRMED') : void {
      this.activeFilter = this.activeFilter == filter ? null : filter;
    }

    // metodo per aprire il modale passando i dati dal DTO cliccato
    openCustomerModal(appointment: AppointmentResponseDTO) {
      this.selectedCustomer.set({
        name: appointment.customerName,
        email: appointment.customerEmail,
        phone: appointment.customerPhone
      });
      this.showCustomerModal.set(true);
    }

    closeCustomerModal() {
      this.showCustomerModal.set(false);
      this.selectedCustomer.set(null);
    }



  }

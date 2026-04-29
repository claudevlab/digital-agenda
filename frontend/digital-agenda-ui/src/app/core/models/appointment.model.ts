export interface AppointmentRequestDTO {
  professionalId: number;
  appointmentDateTime: string; // "YYYY-MM-DDTHH:mm:ss" — LocalDateTime Java
  durationMinutes?: number;
  notes?: string;
}

export interface AppointmentResponseDTO {
  id: number;
  appointmentDate: string;       // LocalDateTime → string in JSON
  durationMinutes: number;
  appointmentStatus: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'REJECTED';
  notes?: string;
  professionalId: number;
  professionalName: string;
  customerId: number;
  customerName: string;

  // nuovi campi per visualizzare il profilo cliente
  customerEmail: string;
  customerPhone: string;
}

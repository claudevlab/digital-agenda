package com.claudev.agenda.service;


import com.claudev.agenda.client.RabbitMQProducer;
import com.claudev.agenda.dto.EmailRequestDTO;
import com.claudev.agenda.entity.Appointment;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.AppointmentStatus;
import com.claudev.agenda.enums.EmailType;
import com.claudev.agenda.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;
    //private final NotificationClient notificationClient;
    private final RabbitMQProducer rabbitMQProducer;
    private final ScheduleExceptionService scheduleExceptionService;

    public AppointmentService(AppointmentRepository appointmentRepository, RabbitMQProducer rabbitMQProducer , ScheduleExceptionService scheduleExceptionService) {
        this.appointmentRepository = appointmentRepository;
        this.rabbitMQProducer = rabbitMQProducer;
        this.scheduleExceptionService = scheduleExceptionService;
    }

    @Transactional
    public Appointment createAppointment(Appointment appointment) {

        // --- INIZIO FIX (VALIDAZIONE BACKEND) ---
        LocalDate appointmentDate = appointment.getAppointmentDateTime().toLocalDate();

        if (scheduleExceptionService.isExceptionDate(appointment.getProfessional(), appointmentDate)) {
            throw new IllegalArgumentException("Impossibile prenotare: il professionista non è disponibile in questa data ");
        }
        // --- FINE FIX ---

        //  Impostiamo la durata di default PRIMA di fare i calcoli sulle date
        if (appointment.getDurationMinutes() == null) {
            appointment.setDurationMinutes(60);
        }

        /*
        Potrebbe verificarsi una condizione di Race Condition in caso di alto traffico
        nel suddetto caso occorre adottare un " pessimisticc Lock sul database usando l'annotation JPA @Lock(LockModeType.PESSIMISTIC_WRITE)
        in modo da serializzare le transazioni per quello specifico utente
        oppure si puo' utilizzare i constraint di esclusione di PostgreSQL per gli intervalli di tempo
         */
        //  LOGICA ANTI DOUBLE-BOOKING
        LocalDateTime reqStart = appointment.getAppointmentDateTime();
        LocalDateTime reqEnd = reqStart.plusMinutes(appointment.getDurationMinutes());

        // Recuperiamo tutti gli slot già occupati (PENDING o CONFIRMED) per quella giornata
        // L'array di LocalDateTime conterra' due intervalli temporali (inizio e fine)
        List<LocalDateTime[]> occupiedSlots = getOccupiedSlotsWithDuration(appointment.getProfessional(), appointmentDate);

        // Verifichiamo se c'è almeno una sovrapposizione usando la Stream API

        /*

        l'array in indice 0 conterra' l'inizio dell'appuntamento esistente
           indice 1 conterra' la fine dello stesso

         */
        boolean isOverlapping = occupiedSlots.stream().anyMatch(occupied -> {
            LocalDateTime exStart = occupied[0];
            LocalDateTime exEnd = occupied[1];

            // Regola aurea per l'intersezione di due intervalli di tempo:
            // L'inizio del nuovo deve essere precedente alla fine dell'esistente E
            // La fine del nuovo deve essere successiva all'inizio dell'esistente
            // confronto dei due valori con il nuovo appuntamento
            // evita gli if annifati "spaghetti code"
            // RICORDA la teoria delle intersezioni degli intervalli
            return reqStart.isBefore(exEnd) && reqEnd.isAfter(exStart);
        });

        if (isOverlapping) {
            throw new IllegalArgumentException("Attenzione: Lo slot richiesto risulta già occupato o si sovrappone a un altro appuntamento.");
        }

        // 4. Nessuna sovrapposizione, possiamo procedere al salvataggio
        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment appointmentSaved = appointmentRepository.save(appointment);

        // imposta stato iniziale
        appointment.setStatus(AppointmentStatus.PENDING);

        // se durata e null imposta di default 1 h
        if (appointment.getDurationMinutes() == null) {
            appointment.setDurationMinutes(60);
        }

        /*
        // invio email utilizzando la stessa applicazione monolitica
        String subject = "Nuova prenotazione : " + appointmentSaved.getId();
        String body = "Ciao " + appointmentSaved.getProfessional().getFirstName() + ", hai un nuova richiesta di appuntamento da "
                + appointmentSaved.getCustomer().getFirstName() + " per il giorno " + appointmentSaved.getAppointmentDateTime();

        // manda email al professionista
        notificationClient.sendEmailNotification(appointmentSaved.getProfessional().getEmail(),subject,body);

         */

        // NEW invio email utilizzando il microservizio con RabbitMQ
        // NEW email in HTML
        // Email al professionista : nuova prenotazione in sospeso
        EmailRequestDTO emailToProfessional = new EmailRequestDTO();
        emailToProfessional.setTo(appointmentSaved.getProfessional().getEmail());
        emailToProfessional.setSubject("Nuova richiesta di appuntamento");
        emailToProfessional.setEmailType(EmailType.NEW_BOOKING_TO_PROFESSIONAL);
        emailToProfessional.setProfessionalName(
                appointmentSaved.getProfessional().getFirstName() + " " +
                        appointmentSaved.getProfessional().getLastName());
        emailToProfessional.setCustomerName(
                appointmentSaved.getCustomer().getFirstName() + " " +
                        appointmentSaved.getCustomer().getLastName());
        emailToProfessional.setAppointmentDateTime(
                appointmentSaved.getAppointmentDateTime().toString());
        emailToProfessional.setDurationMinutes(appointmentSaved.getDurationMinutes());
        emailToProfessional.setNotes(appointmentSaved.getNotes());
        emailToProfessional.setAppUrl("http://localhost:4200");  // reindirizzamento al frontend | DA SISTEMARE |

        // invia la mail in modo asincrono
        rabbitMQProducer.sendEmailNotification(emailToProfessional);

        return appointmentSaved;
    }

    // GET lista appuntamenti customer
    public List<Appointment> getAppointmentByCustomer(User customer) {
        return appointmentRepository.findByCustomer(customer);
    }

    // GET lista appuntamenti professional
    public List<Appointment> getAppointmentByProfessional(User professional) {
        return appointmentRepository.findByProfessional(professional);
    }

    // PUT cambio dello stato appuntamento
    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus newStatus,String reasonRejected) {

        Appointment appointmentSelected = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appuntamento non trovato"));

        appointmentSelected.setStatus(newStatus);

        if (newStatus == AppointmentStatus.REJECTED && reasonRejected != null && !reasonRejected.trim().isEmpty()) {

            appointmentSelected.setReasonRejected(reasonRejected.trim());
        }

        appointmentSelected.setUpdatedAt(LocalDateTime.now());


        Appointment appointmentUpdated = appointmentRepository.save(appointmentSelected);

        // Email che notifica il cambiamento di stato
        String subject = "Aggiornamento appuntamento";
        String body = "Il tuo appuntamento e' stato " + newStatus.name();

        // definiamo il tipo di email prima di utilizzarlo
        EmailType emailType = newStatus == AppointmentStatus.CONFIRMED
                ? EmailType.BOOKING_CONFIRMED_TO_CUSTOMER
                : EmailType.BOOKING_REJECTED_TO_CUSTOMER;

        // notifica al cliente
        EmailRequestDTO emailToCustomer = new EmailRequestDTO();

        // notifica al professionista
        emailToCustomer.setTo(appointmentUpdated.getCustomer().getEmail());
        emailToCustomer.setSubject(newStatus == AppointmentStatus.CONFIRMED
                ? "Appuntamento confermato!" : "Appuntamento non accettato");
        emailToCustomer.setEmailType(emailType);
        emailToCustomer.setCustomerName(
                appointmentUpdated.getCustomer().getFirstName() + " " +
                        appointmentUpdated.getCustomer().getLastName());
        emailToCustomer.setProfessionalName(
                appointmentUpdated.getProfessional().getFirstName() + " " +
                        appointmentUpdated.getProfessional().getLastName());
        emailToCustomer.setAppointmentDateTime(
                appointmentUpdated.getAppointmentDateTime().toString());
        emailToCustomer.setDurationMinutes(appointmentUpdated.getDurationMinutes());
        emailToCustomer.setAppUrl("http://localhost:4200");
        emailToCustomer.setReasonRejected(appointmentUpdated.getReasonRejected());
        rabbitMQProducer.sendEmailNotification(emailToCustomer);

        return appointmentUpdated;
    }

    // DELETE cancella appuntamento

    @Transactional
    public void cancelAppointment(Long id) {

        Appointment appointmentSelected = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appuntamento non trovato"));

        appointmentSelected.setStatus(AppointmentStatus.CANCELLED);
        appointmentSelected.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointmentSelected);

        // logica email
        String professionalFullName = appointmentSelected.getProfessional().getFirstName() +
                " " + appointmentSelected.getProfessional().getLastName();
        String customerFullName = appointmentSelected.getCustomer().getFirstName() +
                " " + appointmentSelected.getCustomer().getLastName();

        // Email al cliente
        EmailRequestDTO emailToCustomer = new EmailRequestDTO();
        emailToCustomer.setTo(appointmentSelected.getCustomer().getEmail());
        emailToCustomer.setSubject("Appuntamento Cancellato");
        emailToCustomer.setEmailType(EmailType.BOOKING_REJECTED_TO_CUSTOMER);
        emailToCustomer.setCustomerName(customerFullName);
        emailToCustomer.setProfessionalName(professionalFullName);
        emailToCustomer.setAppointmentDateTime(appointmentSelected.getAppointmentDateTime().toString());
        emailToCustomer.setDurationMinutes(appointmentSelected.getDurationMinutes());
        emailToCustomer.setAppUrl("http://localhost:4200");
        rabbitMQProducer.sendEmailNotification(emailToCustomer);

        // Email al professionista
        EmailRequestDTO emailToProfessional = new EmailRequestDTO();
        emailToProfessional.setTo(appointmentSelected.getProfessional().getEmail());
        emailToProfessional.setSubject("Appuntamento Cancellato");
        emailToProfessional.setEmailType(EmailType.BOOKING_CANCELLED_TO_PROFESSIONAL);
        emailToProfessional.setCustomerName(customerFullName);
        emailToProfessional.setProfessionalName(professionalFullName);
        emailToProfessional.setAppointmentDateTime(appointmentSelected.getAppointmentDateTime().toString());
        emailToProfessional.setDurationMinutes(appointmentSelected.getDurationMinutes());
        emailToProfessional.setAppUrl("http://localhost:4200");
        rabbitMQProducer.sendEmailNotification(emailToProfessional);

    }


    // Restituisce gli slot orari già occupati per un professionista in una data
    public List<LocalDateTime[]> getOccupiedSlotsWithDuration(User professional, LocalDate date) {

        List<AppointmentStatus> activeStatus = List.of(
                AppointmentStatus.PENDING,
                AppointmentStatus.CONFIRMED);
        // 1-  prendi una data 2 - e falla partire dalle 23:59:59
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // lo stream().map() adatta tutto , anche LocalDataTime in DataTime
        return appointmentRepository.findByProfessionalAndStatusInAndAppointmentDateTimeBetween
                        (professional, activeStatus, startOfDay, endOfDay).stream()
                .map(apt -> new LocalDateTime[]{
                        apt.getAppointmentDateTime(),
                        apt.getAppointmentDateTime().plusMinutes(
                                apt.getDurationMinutes() != null ? apt.getDurationMinutes() : 60)
                })
                .collect(Collectors.toList());
    }

}



package com.claudev.agenda.service;

import com.claudev.agenda.client.NotificationClient;
import com.claudev.agenda.client.RabbitMQProducer;
import com.claudev.agenda.dto.EmailRequestDTO;
import com.claudev.agenda.entity.Appointment;
import com.claudev.agenda.enums.AppointmentStatus;
import com.claudev.agenda.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;
    private final NotificationClient notificationClient;
    private  final RabbitMQProducer rabbitMQProducer;

    public AppointmentService(AppointmentRepository appointmentRepository, NotificationClient notificationClient, RabbitMQProducer rabbitMQProducer) {
        this.appointmentRepository = appointmentRepository;
        this.notificationClient = notificationClient;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Transactional
    public Appointment createAppointment (Appointment appointment) {

        // impostta stato iniziale
        appointment.setStatus(AppointmentStatus.PENDING);

        // se durata e null imposta di default 1 h
        if (appointment.getDurationMinutes() == null) {
            appointment.setDurationMinutes(60);
        }

        Appointment appointmentSaved = appointmentRepository.save(appointment);

        // TO DO logica controllo se il prossimo slot e' libero--> EDIT fatto da un altra parte


        /*
        // invio email
        String subject = "Nuova prenotazione : " + appointmentSaved.getId();
        String body = "Ciao " + appointmentSaved.getProfessional().getFirstName() + ", hai un nuova richiesta di appuntamento da "
                + appointmentSaved.getCustomer().getFirstName() + " per il giorno " + appointmentSaved.getAppointmentDateTime();

        // manda email al professionista
        notificationClient.sendEmailNotification(appointmentSaved.getProfessional().getEmail(),subject,body);

         */

        // NEW invio email utilizzando il microservizio con RabbitMQ
        EmailRequestDTO emailRequest = new EmailRequestDTO(
                appointmentSaved.getProfessional().getEmail(), "Nuova prenotazione RabbitMQ" ,
                "Ciao! Hai una nuova prenotazione (gestita in asincrono)"
                );

        // invia la mail in modo asincrono
        rabbitMQProducer.sendEmailNotification(emailRequest);

        return appointmentSaved;
    }

}

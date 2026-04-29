package com.claudev.agenda.repository;

import com.claudev.agenda.entity.Appointment;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    List <Appointment> findByProfessional (User professional);

    List <Appointment> findByCustomer (User customer);

    List <Appointment> findByProfessionalAndStatus (User professional, AppointmentStatus appointmentStatus);

    // Trova tutti gli appuntamenti CONFERMATI o IN ATTESA di un professionista in un intervallo di tempo
    List<Appointment> findByProfessionalAndStatusInAndAppointmentDateTimeBetween (
            User professional,
            List<AppointmentStatus>statuses,
            LocalDateTime start,
            LocalDateTime end
    );
}

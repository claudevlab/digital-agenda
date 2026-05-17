package com.claudev.agenda.repository;

import com.claudev.agenda.entity.Appointment;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.AppointmentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


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

    // NEW: appuntamenti di un professionista ordinati per data/ora crescente
    List<Appointment> findByProfessionalOrderByAppointmentDateTimeAsc(User professional);

    //  appuntamenti di un customer ordinati per data/ora
    List<Appointment> findByCustomerOrderByAppointmentDateTimeAsc(User customer);

    // filtro appuntamenti attivi (oggi e futuri) -  esclusi passati
    @Query("SELECT a FROM Appointment a WHERE a.professional = :professional " +
            "AND a.appointmentDateTime >= :today " +
            "AND a.status NOT IN ('REJECTED', 'CANCELLED')" +
            "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findActiveAppointment (@Param("professional") User professional ,
                                             @Param("today") LocalDateTime today);


    // filtro appuntamenti storici ( prima della data odierna) - con paginazione
    @Query ("SELECT a FROM Appointment a WHERE a.professional = :professional " +
            "AND a.appointmentDateTime < :today " +
            "ORDER BY a.appointmentDateTime DESC")
    Slice<Appointment> findHistoricalAppointmentAppointments (@Param("professional") User professional ,
                                                              @Param("today")LocalDateTime today,
                                                              Pageable pageable);

}



package com.claudev.agenda.config;

import com.claudev.agenda.entity.Appointment;
import com.claudev.agenda.entity.Schedule;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.AppointmentStatus;
import com.claudev.agenda.enums.DayOfWeek;
import com.claudev.agenda.enums.Role;
import com.claudev.agenda.repository.AppointmentRepository;
import com.claudev.agenda.repository.ScheduleRepository;
import com.claudev.agenda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
/*
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CARICAMENTO DATI DI PROVA...");

        // crea un professionista idraulico ad esempio
        User u1 = new User();
        u1.setFirstName("Tonino");
        u1.setLastName("idraulico");
        u1.setEmail("tonino.idraulico@email.com");
        u1.setUsername("toninooo");
        u1.setPassword("password1234");
        u1.setRole(Role.PROFESSIONAL);
        u1.setPhoneNumber("3331234567");
        u1.setJobTitle("idraulico esperto");
        userRepository.save(u1);

        // creo un cliente

        User c1 = new User();

        c1.setFirstName("gig1");
        c1.setLastName("Arnold");
        c1.setUsername("gigiArnol");
        c1.setEmail("gigi.arnold@email.com");
        c1.setPassword("password123");
        c1.setRole(Role.CUSTOMER);
        c1.setPhoneNumber("3291234567");
        userRepository.save(c1);

        // il professinista imposta il suo orario
        Schedule schedule1 = new Schedule();
        schedule1.setUser(u1);
        schedule1.setDayOfWeek(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(8,0));
        schedule1.setEndTime(LocalTime.of(18,0));
        scheduleRepository.save(schedule1);

        // il cliente prenota
        Appointment appointment1 = new Appointment();
        appointment1.setProfessional(u1);
        appointment1.setCustomer(c1);
        appointment1.setAppointmentDateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        // domani alle 10
        appointment1.setDurationMinutes(60);
        appointment1.setStatus(AppointmentStatus.PENDING);
        appointment1.setNotes("tubo perde acqua");
        appointmentRepository.save(appointment1);

        System.out.println("DATI DI PROVA CARICATI CON SUCCESSO!");
        System.out.println("Creato appuntamento ID: " + appointment1.getId());

    }






}
*/
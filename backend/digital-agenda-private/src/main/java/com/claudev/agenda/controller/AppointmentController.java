package com.claudev.agenda.controller;

import com.claudev.agenda.dto.AppointmentRequestDTO;
import com.claudev.agenda.dto.AppointmentResponseDTO;
import com.claudev.agenda.entity.Appointment;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.mapper.AppointmentMapper;
import com.claudev.agenda.service.AppointmentService;
import com.claudev.agenda.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {


    private final UserService userService;
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;


    public AppointmentController(UserService userService, AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
    }

    /*
    @RequestParam --> il parametro che ci interessa si trova alla fine dell'URL dopo il punto iterrogativo
    si utilizza per filtrare,ordinare,passare dati aggiuntivi
     */
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment (
            @Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO,
            Authentication authentication) {

        // recupero customer
        String customerEmail = authentication.getName(); // injection dati token
        User customer = userService.getUserByEmail(customerEmail).orElseThrow( () ->new RuntimeException("Cliente non trovato"));

        // recupero professional dal DTO
        Long professionalId = appointmentRequestDTO.getProfessionalId();
        User professional = userService.getUserById(professionalId).orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        // conversione DTO - > Entity
        Appointment appointmentEntity = appointmentMapper.toEntity(appointmentRequestDTO);

        // colleghiamo gli utenti
        appointmentEntity.setProfessional(professional);
        appointmentEntity.setCustomer(customer);

        // logica di business
        Appointment appointmentSaved = appointmentService.createAppointment(appointmentEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentMapper.toResponseDTO(appointmentSaved));
    }

}
